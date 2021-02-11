package sqlclient.cli.z_boot.util.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ResourceUtils;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import sqlclient.cli.z_boot.SqlClientApplication;

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
		List<Tuple3<String, Integer, String>> lines = new ArrayList<>();
		String helpWith = this.getValue("help");
		if(StringUtils.isBlank(helpWith)) {
			lines.addAll(getHelpMenuLines());
		}else{
			lines.addAll(getHelpLinesForOption(helpWith));
		}

		System.out.println("SQL command-line tool.  ");
		int maxLength = lines.stream().map(item -> item._2).max(Integer::compare).get();
		lines.forEach(item -> {
			System.out.println(StringUtils.rightPad(item._1, maxLength, " ") + " " + item._3);
		});
		System.out.println(footer);
	}
	private List<Tuple3<String, Integer, String>> getHelpMenuLines() {
		List<Tuple3<String, Integer, String>> out = new ArrayList<>();
		this.allOptions
		.stream()
		.filter(item -> item.isIncludeInHelp())
		.forEach(item -> {
			String line = buildHelpLine(item, 2);
			out.add(Tuple.of(line, line.length(), item.getDescription()));
		});
		this.allOptionGroups.forEach(item -> {
			String line = buildHelpLine(item, 2);
			out.add(Tuple.of(line, line.length(), item.getName()));

			item.getOptions().stream()
			.filter(i -> i.isIncludeInHelp())
			.forEach(i -> {
				String l = buildHelpLine(i, 6);
				out.add(Tuple.of(l, l.length(), i.getDescription()));
			});
		});
		return out;
	}
	private List<Tuple3<String, Integer, String>> getHelpLinesForOption(String helpWith){
		CommandLineOption option = this.allOptions
				.stream()
				.filter(item -> {
					if(StringUtils.equals(item.getLongName(), helpWith)) {
						return true;
					}
					return helpWith.length()==1 && helpWith.charAt(0) == item.getShortName();
				})
				.findFirst()
				.orElse(null);
		if(option == null) {
			return getHelpMenuLines();
		}
		List<String> out = new ArrayList<>();
		out.add("Usage:");
		if(option.getShortName() != null) {
			if(option.isHasValue()) {
				out.add("	-" + option.getShortName() + "<arg>");
				out.add("	-" + option.getShortName() + " <arg>");
				out.add("	-" + option.getShortName() + "=<arg>");
			}else{
				out.add("	-" + option.getShortName());
			}
		}
		if(option.getLongName() != null) {
			if(option.isHasValue()) {
				out.add("	--" + option.getLongName() + " <arg>");
				out.add("	--" + option.getLongName() + "=<arg>");
			}else{
				out.add("	--" + option.getLongName());
			}
		}
		List<String> file = getHelpFileContent(option.getLongName());
		if(file == null) {
			file = getHelpFileContent(option.getShortName() == null ? null : option.getShortName().toString());
		}
		if(file != null) {
			out.addAll(file);
		}

		out.add("");
		return out.stream()
				.map(item -> Tuple.of(item,0,""))
				.collect(Collectors.toList());
	}
	private List<String> getHelpFileContent(String name) {
		String fileName = "help-"+ name +".txt";
		try {
			return FileUtils.readLines(ResourceUtils.getFile("classpath:help-" + name +".txt"), Charset.defaultCharset());
		}catch (IOException e) {
			InputStream inputStream = CommandLine.class.getClassLoader().getResourceAsStream("BOOT-INF/classes/"+ fileName);
			if(inputStream == null) {
				return null;
			}
			try {
				return IOUtils.readLines(inputStream, Charset.defaultCharset());
			} catch (IOException e1) {
				return null;
			}
		}
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
