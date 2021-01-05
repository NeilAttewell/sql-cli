package sqlclient.cli.contracts;

import sqlclient.cli.domain.Query;

/**
 * @author Neil Attewell
 */
public interface ICommand {
	public String getCommand();
	public boolean isCommand(Query query);
}
