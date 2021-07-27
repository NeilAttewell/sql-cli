package sqlclient.core.executors;

import org.apache.commons.lang.StringUtils;

import sqlclient.core.contracts.ICommand;
import sqlclient.core.contracts.IInputExecutor;
import sqlclient.core.domain.Query;

/**
 * @author Neil Attewell
 */
public abstract class AbstractCommandExecutor implements IInputExecutor, ICommand{
	@Override
	public boolean isCommand(Query query) {
		return StringUtils.equalsIgnoreCase(StringUtils.trim(query.getQuery()), getCommand());
	}
	@Override
	public boolean canExecute(Query query) {
		return isCommand(query);
	}
}
