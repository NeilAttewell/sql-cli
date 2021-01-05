package sqlclient.cli.contracts;

import java.sql.SQLException;

import sqlclient.cli.domain.Query;
import sqlclient.cli.exceptions.ExitException;

/**
 * @author Neil Attewell
 */
public interface IInputExecutor {
	boolean isDefault();
	boolean canExecute(Query query);
	void execute(Query query) throws ExitException, SQLException;
}
