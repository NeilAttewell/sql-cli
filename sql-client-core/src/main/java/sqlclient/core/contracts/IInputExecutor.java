package sqlclient.core.contracts;

import java.sql.SQLException;

import sqlclient.core.domain.Query;
import sqlclient.core.exceptions.ExitException;

/**
 * @author Neil Attewell
 */
public interface IInputExecutor {
	boolean isDefault();
	boolean canExecute(Query query);
	void execute(Query query) throws ExitException, SQLException;
}
