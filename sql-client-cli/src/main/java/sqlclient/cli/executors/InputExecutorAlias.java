package sqlclient.cli.executors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.cli.QueryAliasRegistry;
import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.contracts.IQueryResult;
import sqlclient.cli.domain.DisplayTypeEnum;
import sqlclient.cli.domain.Query;
import sqlclient.cli.printer.IResultSetPrinter;

/**
 * @author Neil Attewell
 * 
 * List aliases:	alias
 * View alias:		alias some alias
 */
@Component
public class InputExecutorAlias extends AbstractCommandExecutor{
	@Autowired private SpecialCharacterRegistry characterRegistry;
	@Autowired private QueryAliasRegistry aliasRegistry;

	@Autowired private List<? extends IResultSetPrinter> resultSetPrinters;

	private static final Pattern PATTERN_LIST_ALIAS = Pattern.compile("^alias$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PATTERN_VIEW_ALIAS = Pattern.compile("^alias\\s+(?<name>([a-zA-Z_]\\s*[_a-zA-Z0-9]*)+)$", Pattern.CASE_INSENSITIVE);
	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public String getCommand() {
		return "alias";
	}
	@Override
	public boolean isCommand(Query input) {
		String query = input.getQuery().trim();
		if(PATTERN_LIST_ALIAS.matcher(query).matches()) {
			return true;
		}
		if(PATTERN_VIEW_ALIAS.matcher(query).matches()) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(Query input) throws SQLException {
		String query = input.getQuery().trim();

		Matcher matcher = PATTERN_LIST_ALIAS.matcher(query);
		if(matcher.matches()) {
			printAliasList();
			return;
		}

		matcher = PATTERN_VIEW_ALIAS.matcher(query);
		if(matcher.matches()) {
			printAlias(matcher);
			return;
		}
	}
	
	private void printAliasList() throws SQLException {
		AliasListResultSet resultSet = new AliasListResultSet(this.aliasRegistry.getAliasNames());

		getPrinter(this.resultSetPrinters, item -> item.canPrintForResultSet(DisplayTypeEnum.Table), IResultSetPrinter::isDefaultForResultSet)
		.print(resultSet, 0L);
	}
	private void printAlias(Matcher matcher) throws SQLException {
		String name = matcher.group("name");
		var query = this.aliasRegistry.getAlias(name, this.characterRegistry.getPrimaryDelimiter());
		AliasViewResultSet result;
		if(query == null) {
			result=new AliasViewResultSet(this.characterRegistry, null, Collections.EMPTY_LIST);
		}else{
			result=new AliasViewResultSet(this.characterRegistry, name, query);
		}
		getPrinter(this.resultSetPrinters, item -> item.canPrintForResultSet(DisplayTypeEnum.Table), IResultSetPrinter::isDefaultForResultSet)
		.print(result, 0L);
	}

	private <T> T getPrinter(List<T> list, Predicate<T> displayTypeFilter, Predicate<T> defaultFilter) {
		T out = list.stream().filter(displayTypeFilter).findFirst().orElse(null);
		if(out != null) {
			return out;
		}
		return list.stream().filter(defaultFilter).findFirst().orElse(null);
	}



	protected static class AliasListResultSet implements IQueryResult{
		private final List<String> values;
		protected int index;
		public AliasListResultSet(List<String> values) {
			this.values=values;
			this.index=-1;
		}
		@Override
		public int getColumnCount(){
			return 1;
		}
		@Override
		public String getColumnName(int index){
			return "alias";
		}
		@Override
		public int getColumnDisplaySize(int index){
			return getColumnName(index).length();
		}
		@Override
		public boolean next(){
			this.index++;
			return this.values.size() > this.index;
		}
		@Override
		public Object getObject(int columnIndex){
			return this.values.get(this.index);
		}
	}
	protected static class AliasViewResultSet extends AliasListResultSet{
		private final String name;
		public AliasViewResultSet(SpecialCharacterRegistry characterRegistry, String name, List<Query> values) {
			super(convert(characterRegistry, values));
			this.name=name;
		}
		private static List<String> convert(SpecialCharacterRegistry characterRegistry, List<Query> values){
			return values.stream().map(item -> {
				if(StringUtils.isBlank(item.getDelimiter())) {
					return item.getQuery() + characterRegistry.getPrimaryDelimiter();
				}
				return item.getQuery() + item.getDelimiter();

			}).collect(Collectors.toList());
		}
		@Override
		public int getColumnCount(){
			return 2;
		}
		@Override
		public String getColumnName(int index){
			if(index == 1) {
				return super.getColumnName(index);
			}
			return "query";
		}
		@Override
		public Object getObject(int index){
			if(index != 1) {
				return super.getObject(index);
			}
			if(super.index == 0) {
				return this.name;
			}
			return "";
		}
	}
}