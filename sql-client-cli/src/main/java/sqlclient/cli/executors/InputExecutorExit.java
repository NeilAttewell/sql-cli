package sqlclient.cli.executors;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.core.ApplicationState;
import sqlclient.core.contracts.IOutputSink;
import sqlclient.core.domain.Query;
import sqlclient.core.exceptions.ExitException;
import sqlclient.core.executors.AbstractCommandExecutor;

/**
 * @author Neil Attewell
 */
@Component
public class InputExecutorExit extends AbstractCommandExecutor{
	@Autowired private Connection connection;
	@Autowired private ApplicationState state;
	@Autowired private IOutputSink outputSink;

	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public String getCommand() {
		return "exit";
	}
	@Override
	public void execute(Query query) throws ExitException {
		try {
			if(this.state.isAutoCommit()) {
				this.connection.commit();
				this.connection.close();
				throw new ExitException();
			}

			this.outputSink.printInfo("Auto commiting your transaction");
			this.connection.commit();
			this.connection.close();
			throw new ExitException();
		}catch (SQLException e) {
			throw new ExitException(e);
		}
	}
}