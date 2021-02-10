package sqlclient.cli.executors;

import org.apache.commons.lang.StringUtils;

import sqlclient.cli.contracts.ICommand;
import sqlclient.cli.contracts.IInputExecutor;
import sqlclient.cli.domain.Query;

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
