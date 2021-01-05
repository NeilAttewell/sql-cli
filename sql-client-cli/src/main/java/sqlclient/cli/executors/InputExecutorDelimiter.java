package sqlclient.cli.executors;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.contracts.ICommand;
import sqlclient.cli.contracts.IInputExecutor;
import sqlclient.cli.domain.Query;
import sqlclient.cli.exceptions.ExitException;

/**
 * @author Neil Attewell
 */
@Component
public class InputExecutorDelimiter implements IInputExecutor, ICommand{
	private static final Pattern PATTERN = Pattern.compile("^\\s*delimiter\\s+(?<delimiter>\\p{Punct}+)$",Pattern.CASE_INSENSITIVE);
	@Autowired private SpecialCharacterRegistry characterRegistry;
	
	
	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public String getCommand() {
		return "delimiter";
	}
	@Override
	public boolean isCommand(Query query) {
		String sQuery = query.getQuery();
		Matcher matcher = PATTERN.matcher(sQuery);
		if(!matcher.matches()) {
			return false;
		}
		String delimiter = matcher.group("delimiter");
		
		for(String wrapper : this.characterRegistry.getWrapperStrings()) {
			if(StringUtils.containsIgnoreCase(delimiter, wrapper)) {
				return false;
			}
		}
		for(String item : this.characterRegistry.getEnabledDelimiters()) {
			if(StringUtils.equalsIgnoreCase(delimiter, item)) {
				return false;
			}
		}
		return true;
	}
	
	
	
	@Override
	public boolean canExecute(Query query) {
		return isCommand(query);
	}
	@Override
	public void execute(Query query) throws ExitException, SQLException {
		Matcher matcher = PATTERN.matcher(query.getQuery());
		matcher.matches();
		this.characterRegistry.setPrimaryDelimiter(matcher.group("delimiter"));
	}
}
