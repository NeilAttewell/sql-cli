package sqlclient.cli.z_boot.util.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;

import io.vavr.Tuple;
import io.vavr.Tuple3;

/**
 * @author Neil Attewell
 *	Modeled after: https://commons.apache.org/proper/commons-cli/index.html
 *	Not completely compatible yet
 */

public class CommandLine {
	private final CommandLineArgument helpArgument;
	private final List<CommandLineArgument> arguments;
	private final List<CommandLineOptionGroup> activeGroups;
	private final List<CommandLineOptionGroup> allOptionGroups;
	private final List<CommandLineOption> allOptions;

	protected CommandLine(List<CommandLineArgument> arguments, List<CommandLineOptionGroup> activeGroups, CommandLineArgument helpArgument, List<CommandLineOption> allOptions, List<CommandLineOptionGroup> allOptionGroups) {
		this.arguments=arguments;
		this.activeGroups=activeGroups;
		this.helpArgument=helpArgument;
		this.allOptions=allOptions;
		this.allOptionGroups=allOptionGroups;
	}
	private Optional<CommandLineArgument> getArgument(Predicate<CommandLineOption> predicate) {
		return this.arguments.stream()
				.filter(item -> predicate.test(item.getOption()))
				.findFirst();
	}
	private List<String> getValues(Predicate<CommandLineOption> predicate) {
		return getArgument(predicate)
				.map(item -> item.getValues())
				.orElse(null);
	}
	private String getValue(Predicate<CommandLineOption> predicate) {
		return getArgument(predicate)
				.stream()
				.map(item -> item.getValues())
				.flatMap(item -> item.stream())
				.findFirst()
				.orElse(null);
	}
	private boolean isFound(Predicate<CommandLineOption> predicate) {
		return getArgument(predicate).isPresent();
	}
	public List<CommandLineOptionGroup> getActiveGroups(){
		return this.activeGroups;
	}

	//============================================================================================================
	//		Character
	//============================================================================================================
	public List<String> getValues(char name) {
		return getValues(item -> findByShortName(item, name));
	}
	public String getValue(char name) {
		return getValue(item -> findByShortName(item, name));
	}
	public String getValue(char name, String defaultValue) {
		String out = getValue(item -> findByShortName(item, name));
		if(out != null) {
			return out;
		}
		return defaultValue;
	}
	public boolean isFound(char name) {
		return isFound(item -> findByShortName(item, name));
	}

	//============================================================================================================
	//		String
	//============================================================================================================
	public List<String> getValues(String name) {
		return getValues(item -> findByLongName(item, name));
	}
	public String getValue(String name) {
		return getValue(item -> findByLongName(item, name));
	}
	public String getValue(String name, String defaultValue) {
		String out = getValue(item -> findByLongName(item, name));
		if(out != null) {
			return out;
		}
		return defaultValue;
	}
	public boolean isFound(String name) {
		return isFound(item -> findByLongName(item, name));
	}


	private boolean findByShortName(CommandLineOption option, Character input){
		return option.getShortName() != null && option.getShortName().equals(input);
	}
	private boolean findByLongName(CommandLineOption option, String input){
		return option.getLongName() != null && option.getLongName().equals(input);
	}
	
	//============================================================================================================
	//		Help
	//============================================================================================================
	public boolean isPrintHelp() {
		return this.helpArgument != null;
	}
	public void printHelp(String footer) {
		System.out.println("SQL command-line tool.  ");
		List<Tuple3<String, Integer, String>> lines = new ArrayList<>();
		this.allOptions
		.stream()
		.filter(item -> item.isIncludeInHelp())
		.forEach(item -> {
			String line = buildHelpLine(item, 2);
			lines.add(Tuple.of(line, line.length(), item.getDescription()));
		});
		this.allOptionGroups.forEach(item -> {
			String line = buildHelpLine(item, 2);
			lines.add(Tuple.of(line, line.length(), item.getName()));
			
			item.getOptions().stream()
			.filter(i -> i.isIncludeInHelp())
			.forEach(i -> {
				String l = buildHelpLine(i, 6);
				lines.add(Tuple.of(l, l.length(), i.getDescription()));
			});
		});
		
		int maxLength = lines.stream().map(item -> item._2).max(Integer::compare).get();
		lines.forEach(item -> {
			System.out.println(StringUtils.rightPad(item._1, maxLength, " ") + " " + item._3);
		});
		System.out.println(footer);
	}
	
	private String buildHelpLine(CommandLineOption option, int indent) {
		String out = StringUtils.repeat(" ", indent);
		String seperator = "";
		
		if(option.getShortName() != null) {
			seperator = ",";
			out += "-" + option.getShortName();
		}
		if(option.getLongName() != null) {
			out += seperator + "--" + option.getLongName();
		}
		if(option.isHasValue()) {
			out += " <arg>"; 
		}
		return out;
	}
	private String buildHelpLine(CommandLineOptionGroup optionGroup, int indent) {
		String out = StringUtils.repeat(" ", indent);
		String seperator = "";
		
		var option = optionGroup.getConditionOption();
		
		if(option.getShortName() != null) {
			seperator = ",";
			out += "-" + option.getShortName();
		}
		if(option.getLongName() != null) {
			out += seperator + "--" + option.getLongName();
		}
		if(option.isHasValue()) {
			out +=  " = \"" + optionGroup.getConditionValue() + "\"";
		}
		return out;
	}
	
	//============================================================================================================
	//		Help
	//============================================================================================================
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommandLine:[\n");
		this.arguments.forEach(item -> {
			builder.append("ShortName:[").append(item.getOption().getShortName()).append("]");
			builder.append(" LongName:[").append(item.getOption().getLongName()).append("]");
			builder.append(" HasValue:[").append(item.getOption().isHasValue()).append("]");
			builder.append(" Values:").append(item.getValues());
			builder.append("\n");
		});
		builder.append("]");
		return builder.toString();
	}
}
