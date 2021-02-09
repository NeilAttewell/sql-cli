package sqlclient.cli.executors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.cli.contracts.ICommand;
import sqlclient.cli.contracts.IInputExecutor;
import sqlclient.cli.contracts.IQueryResult;
import sqlclient.cli.domain.DisplayTypeEnum;
import sqlclient.cli.domain.Query;
import sqlclient.cli.exceptions.ExitException;
import sqlclient.cli.printer.IResultSetPrinter;

public abstract class AbstractPatternCommandExecutor implements IInputExecutor, ICommand{
	@Autowired private List<? extends IResultSetPrinter> resultSetPrinters;

	private final List<Tuple2<Pattern, HandleQueryFunction>> patterns;
	protected AbstractPatternCommandExecutor() {
		this.patterns=new ArrayList<>();
	}
	protected AbstractPatternCommandExecutor(List<Tuple2<Pattern, HandleQueryFunction>> patterns) {
		this.patterns=patterns;
	}
	protected void addPattern(Pattern pattern, HandleQueryFunction handler) {
		this.patterns.add(Tuple.of(pattern, handler));
	}

	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public boolean canExecute(Query query) {
		return isCommand(query);
	}
	@Override
	public boolean isCommand(Query input) {
		String query = input.getQuery().trim();
		for(var item : this.patterns) {
			if(item._1.matcher(query).matches()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void execute(Query input) throws ExitException, SQLException {
		String query = input.getQuery().trim();
		var tuple = this.patterns.stream()
				.map(item -> item.append(item._1.matcher(query)))
				.filter(item -> item._3.matches())
				.findFirst()
				.orElse(null);
		if(tuple == null) {
			return;
		}
		IQueryResult result = tuple._2.handle(input, tuple._3);
		if(result == null) {
			return;
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

	@FunctionalInterface
	public interface HandleQueryFunction{
		IQueryResult handle(Query query, Matcher matcher) throws ExitException, SQLException;
	}
}
