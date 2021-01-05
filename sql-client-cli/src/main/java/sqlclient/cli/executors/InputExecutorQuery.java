package sqlclient.cli.executors;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.cli.contracts.IInputExecutor;
import sqlclient.cli.domain.DisplayTypeEnum;
import sqlclient.cli.domain.Query;
import sqlclient.cli.domain.QueryResultSet;
import sqlclient.cli.printer.IResultSetPrinter;
import sqlclient.cli.printer.IResultUpdatePrinter;

/**
 * @author Neil Attewell
 */
@Component
public class InputExecutorQuery implements IInputExecutor{
	@Autowired private Connection connection;
	@Autowired private List<? extends IResultSetPrinter> resultSetPrinters;
	@Autowired private List<? extends IResultUpdatePrinter> resultUpdatePrinters;

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
		try(Statement statement = this.connection.createStatement()){
			DisplayTypeEnum displayType = DisplayTypeEnum.parse(query.getDelimiter());

			long startTime=System.currentTimeMillis();
			boolean hasResultSet = statement.execute(query.getQuery());
			if(hasResultSet) {
				getPrinter(this.resultSetPrinters, item -> item.canPrintForResultSet(displayType), IResultSetPrinter::isDefaultForResultSet)
				.print(new QueryResultSet(statement.getResultSet()), System.currentTimeMillis()-startTime);
			}else {
				getPrinter(this.resultUpdatePrinters, item -> item.canPrintForUpdate(displayType), IResultUpdatePrinter::isDefaultForUpdate)
				.print(statement.getUpdateCount(), System.currentTimeMillis()-startTime);
			}
		}
	}
	private <T> T getPrinter(List<T> list, Predicate<T> displayTypeFilter, Predicate<T> defaultFilter) {
		T out = list.stream().filter(displayTypeFilter).findFirst().orElse(null);
		if(out != null) {
			return out;
		}
		return list.stream().filter(defaultFilter).findFirst().orElse(null);
	}
}