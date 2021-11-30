package sqlclient.cli.executors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.cli.domain.QueryResultSet;
import sqlclient.core.ApplicationState;
import sqlclient.core.contracts.IInputExecutor;
import sqlclient.core.contracts.IResultSetPrinter;
import sqlclient.core.contracts.IResultUpdatePrinter;
import sqlclient.core.contracts.IVariableStore;
import sqlclient.core.domain.DisplayTypeEnum;
import sqlclient.core.domain.Query;
import sqlclient.core.domain.QueryPart;

/**
 * @author Neil Attewell
 */
@Component
public class InputExecutorQuery implements IInputExecutor{
	private static final Pattern PATTERN = Pattern.compile("([:@](?<name>[a-zA-Z_]\\w*))",Pattern.CASE_INSENSITIVE);
	@Autowired private Connection connection;
	@Autowired private List<? extends IResultSetPrinter> resultSetPrinters;
	@Autowired private List<? extends IResultUpdatePrinter> resultUpdatePrinters;
	@Autowired private ApplicationState applicationState;


	@Override
	public boolean isDefault() {
		return true;
	}
	@Override
	public boolean canExecute(Query query) {
		return true;
	}
	@Override
	public void execute(Query query) throws SQLException {
		Tuple2<Query,List<String>> tuple = parseForParameterNames(query);
		if(tuple._2.isEmpty()) {
			executeSimple(query);
		}else{
			executeWithParameters(tuple._1, tuple._2);
		}
	}

	private void executeSimple(Query query) throws SQLException {
		try(Statement statement = this.connection.createStatement()){
			DisplayTypeEnum displayType = DisplayTypeEnum.parse(query.getDelimiter());

			long startTime=System.currentTimeMillis();
			String sql = replaceHandlebars(query.getQuery());
			boolean hasResultSet = statement.execute(sql);
			handleExecutionResult(statement, hasResultSet, startTime, displayType);
		}
	}
	private void executeWithParameters(Query query, List<String> parameters) throws SQLException {
		String sql = replaceHandlebars(query.getQuery());
		try(PreparedStatement statement = this.connection.prepareStatement(sql)){
			DisplayTypeEnum displayType = DisplayTypeEnum.parse(query.getDelimiter());
			long startTime=System.currentTimeMillis();

			for(int i = 0 ; i < parameters.size() ; i++) {
				String parameter = this.applicationState.getVariableStoreLastQueryResult().get(parameters.get(i));
				if(StringUtils.isBlank(parameter)) {
					parameter = this.applicationState.getVariableStoreUser().get(parameters.get(i));
				}
				statement.setObject(i+1, parameter);
			};
			boolean hasResultSet = statement.execute();
			handleExecutionResult(statement, hasResultSet, startTime, displayType);
		}
	}
	private void handleExecutionResult(Statement statement, boolean hasResultSet, long startTime, DisplayTypeEnum displayType) throws SQLException {
		if(hasResultSet) {
			var printer = getPrinter(this.resultSetPrinters, item -> item.canPrintForResultSet(displayType), IResultSetPrinter::isDefaultForResultSet);
			QueryResultSet resultSet = new QueryResultSet(statement.getResultSet());
			this.applicationState.getVariableStoreLastQueryResult().clearAll();
			if(resultSet.next()) {
				for(int i = 1 ; i <= resultSet.getColumnCount() ; i++) {
					Object value = resultSet.getObject(i);
					if(value != null) {
						this.applicationState.getVariableStoreLastQueryResult().set(resultSet.getColumnName(i), ""+value);
					}
				}
			}
			resultSet.doSkipNext();
			printer.print(resultSet, System.currentTimeMillis()-startTime);
			while(statement.getMoreResults()) {
				resultSet = new QueryResultSet(statement.getResultSet());
				printer.print(resultSet, System.currentTimeMillis()-startTime);
			}
		}else {
			getPrinter(this.resultUpdatePrinters, item -> item.canPrintForUpdate(displayType), IResultUpdatePrinter::isDefaultForUpdate)
			.print(statement.getUpdateCount(), System.currentTimeMillis()-startTime);
		}
	}

	private Tuple2<Query,List<String>> parseForParameterNames(Query query){
		List<String> parameterNames = new ArrayList<>();
		List<QueryPart> parts = query.getParts().stream()
				.map(item -> {
					if(item.isWrapped()) {
						return item;
					}
					String out = item.getValue();

					Matcher matcher = PATTERN.matcher(item.getValue());
					while ( matcher.find() ) {
						out = StringUtils.replaceOnce(out, matcher.group(0), "?");
						parameterNames.add(matcher.group("name"));
					}
					return new QueryPart(out, false);
				})
				.collect(Collectors.toList());
		return Tuple.of(new Query(parts, query.getDelimiter(), query.isPartOfMultiQuery()), parameterNames);
	}
	private String replaceHandlebars(String sql) {
		var list = List.of(this.applicationState.getVariableStoreUser(),this.applicationState.getVariableStoreLastQueryResult());

		for(IVariableStore store : list) {
			for(String name : store.getNames()) {
				String search = "{{" + name + "}}";
				int index;
				while((index = StringUtils.indexOfIgnoreCase(sql, search)) != -1) {
					search = StringUtils.substring(sql, index, index+search.length());
					sql = StringUtils.replaceOnce(sql, search, store.get(name));
				}
			}
		}
		return sql;
	}

	private <T> T getPrinter(List<T> list, Predicate<T> displayTypeFilter, Predicate<T> defaultFilter) {
		T out = list.stream().filter(displayTypeFilter).findFirst().orElse(null);
		if(out != null) {
			return out;
		}
		return list.stream().filter(defaultFilter).findFirst().orElse(null);
	}
}