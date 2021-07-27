package sqlclient.core.contracts;

import sqlclient.core.domain.Query;

/**
 * @author Neil Attewell
 */
public interface ICommand {
	public String getCommand();
	public boolean isCommand(Query query);
}
