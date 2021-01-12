package sqlclient.cli.executors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.contracts.ICommand;
import sqlclient.cli.contracts.IInputExecutor;
import sqlclient.cli.domain.Query;

/**
 * @author Neil Attewell
 */
public abstract class AbstractCommandExecutor implements IInputExecutor, ICommand{
	@Autowired private SpecialCharacterRegistry characterRegistry;

	@Override
	public boolean isCommand(Query query) {
		for(String delimiter : this.characterRegistry.getEnabledDelimiters()) {
			String tmp = StringUtils.trim(query.getQuery());
			if(StringUtils.equalsIgnoreCase(tmp, getCommand() + delimiter)) {
				return true;
			}
		}
		return StringUtils.equalsIgnoreCase(StringUtils.trim(query.getQuery()), getCommand());
	}
	@Override
	public boolean canExecute(Query query) {
		return isCommand(query);
	}
}
