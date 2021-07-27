package sqlclient.core.util.cli;

import java.util.List;

/**
 * @author Neil Attewell
 */
public class CommandLineArgument {
	private final CommandLineOption option;
	private final List<String> values;
	public CommandLineArgument(CommandLineOption option, List<String> values) {
		super();
		this.option = option;
		this.values = values;
	}
	public CommandLineOption getOption() {
		return option;
	}
	public List<String> getValues() {
		return values;
	}
	
}
