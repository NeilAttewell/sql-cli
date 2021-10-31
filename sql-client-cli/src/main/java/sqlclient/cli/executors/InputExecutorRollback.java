package sqlclient.cli.executors;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.core.ApplicationState;
import sqlclient.core.contracts.IOutputSink;
import sqlclient.core.domain.Query;
import sqlclient.core.executors.AbstractCommandExecutor;

/**
 * @author Neil Attewell
 */
@Component
public class InputExecutorRollback extends AbstractCommandExecutor{
	@Autowired private Connection connection;
	@Autowired private ApplicationState state;
	@Autowired private IOutputSink outputSink;

	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public String getCommand() {
		return "rollback";
	}
	@Override
	public void execute(Query query) throws SQLException {
		try {
			this.connection.rollback();
		} catch (SQLException e) {
			this.outputSink.printError("Failed to rollback transaction.  Reason:[" + e.getMessage() + "]");
		}
	}
}