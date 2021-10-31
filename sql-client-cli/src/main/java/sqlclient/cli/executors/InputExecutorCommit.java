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
public class InputExecutorCommit extends AbstractCommandExecutor{
	@Autowired private Connection connection;
	@Autowired private ApplicationState state;
	@Autowired private IOutputSink outputSink;

	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public String getCommand() {
		return "commit";
	}
	@Override
	public void execute(Query query) throws SQLException {
		try {
			this.connection.commit();
		} catch (SQLException e) {
			this.outputSink.printError("Failed to commit transaction.  Reason:[" + e.getMessage() + "]");
		}
	}
}