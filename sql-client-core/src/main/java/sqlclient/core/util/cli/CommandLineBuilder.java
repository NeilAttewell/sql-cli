package sqlclient.core.util.cli;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import io.vavr.Tuple;
import io.vavr.Tuple2;

/**
 * @author Neil Attewell
 */
public class CommandLineBuilder {
	private CommandLineOption helpOption;
	private CommandLineOption propertiesOption;
	private List<CommandLineOption> options = new ArrayList<>();
	private List<CommandLineOptionGroup> optionGroups = new ArrayList<>();
	private String[] arguments = new String[0];

	public static CommandLineBuilder builder() {
		return new CommandLineBuilder();
	}
	
	public CommandLineBuilder addHelpOption(CommandLineOption option) {
		this.helpOption = option;
		this.options.add(option);
		return this;
	}
	public CommandLineBuilder addPropertiesOption(CommandLineOption option) {
		this.propertiesOption = option;
		this.options.add(option);
		return this;
	}
	public CommandLineBuilder addOption(CommandLineOption option) {
		this.options.add(option);
		return this;
	}
	public CommandLineBuilder addOptions(Collection<CommandLineOption> options) {
		this.options.addAll(options);
		return this;
	}
	public CommandLineOption.OptionBuilder addOption() {
		return new CommandLineOption.OptionBuilder(this);
	}

	public CommandLineBuilder addOptionGroup(CommandLineOptionGroup optionGroup) {
		this.optionGroups.add(optionGroup);
		return this;
	}
	public CommandLineBuilder addOptionGroups(Collection<CommandLineOptionGroup> optionGroups) {
		this.optionGroups.addAll(optionGroups);
		return this;
	}
	public CommandLineOptionGroup.OptionGroupBuilder addOptionGroup() {
		return new CommandLineOptionGroup.OptionGroupBuilder(this);
	}

	public CommandLineBuilder withArguments(String[] arguments) {
		this.arguments = arguments;
		return this;
	}
	public CommandLineBuilder addArgument(String argument) {
		this.arguments = (String[]) ArrayUtils.add(this.arguments, argument);
		return this;
	}

	private List<String> loadProperties(List<String> paths) {
		List<String> out = new ArrayList<>();
		
		for(String path : paths) {
			try {
				File file = new File(path);
				if(!file.exists()) {
					continue;
				}
				FileUtils.readLines(file, Charset.forName("UTF-8"))
				.stream()
				.filter(item -> StringUtils.startsWith(item, "-"))
				.forEach(item -> out.add(item));
			}catch (Exception e) {
				System.err.println("Failed to load properties file: [" + path +"].  Error:[" + e.getMessage() + "]");
			}
		}
		return out;
	}
	
	
	public CommandLine build() {
		return build(null);
	}
	public CommandLine build(String[] arguments) {
		arguments = (String[]) ArrayUtils.addAll(this.arguments, arguments);
		
		Tuple2<List<CommandLineArgument>, List<CommandLineOptionGroup>> result = parse(arguments, this.options, this.optionGroups);

		if(this.propertiesOption != null) {
			CommandLineArgument properties = result._1.stream().filter(item -> item.getOption() == this.propertiesOption).findFirst().orElse(null);
			if(properties != null) {
				List<String> argumentList = new ArrayList<>();
				argumentList.addAll(Arrays.asList(arguments));
				argumentList.addAll(loadProperties(properties.getValues()));
				result = parse(argumentList.toArray(new String[0]), this.options, this.optionGroups);
			}
		}
		
		if(this.helpOption == null) {
			return new CommandLine(result._1, result._2, null, this.options, this.optionGroups);
		}
		CommandLineArgument helpArgument = result._1.stream()
				.filter(item -> item.getOption() == this.helpOption)
				.findFirst()
				.orElse(null);
		return new CommandLine(result._1, result._2, helpArgument, this.options, this.optionGroups);
	}
	


	private static Tuple2<List<CommandLineArgument>, List<CommandLineOptionGroup>> parse(String[] arguments, List<CommandLineOption> options, List<CommandLineOptionGroup> groups) {
		if(groups.isEmpty()) {
			return Tuple.of(parse(arguments, options), Collections.EMPTY_LIST);
		}

		List<CommandLineArgument> outArguments = new ArrayList<>();
		outArguments.addAll(parse(arguments, options));

		List<CommandLineOptionGroup> outOptionGroups = new ArrayList<>();
		for(CommandLineOptionGroup group : groups) {
			List<CommandLineOption> tmp = new ArrayList<>(group.getOptions());
			tmp.add(group.getConditionOption());

			List<CommandLineArgument> list = parse(arguments, tmp);
			Long count = list.stream()
					.filter(item ->{
						if(group.getConditionOption().getShortName() != null && group.getConditionOption().getShortName().equals(item.getOption().getShortName())) {
							return true;
						}
						if(group.getConditionOption().getLongName() != null && group.getConditionOption().getLongName().equals(item.getOption().getLongName())) {
							return true;
						}
						return false;
					})
					.filter(item -> {
						if(!group.getConditionOption().isHasValue()) {
							return true;
						}
						if(item.getValues() == null) {
							return false;
						}
						return item.getValues().stream()
								.findFirst()
								.filter(value -> StringUtils.equalsIgnoreCase(group.getConditionValue(), value))
								.isPresent();
					}).count();
			if(count > 0) {
				outOptionGroups.add(group);
				outArguments.addAll(list);
			}
		}
		return Tuple.of(outArguments, outOptionGroups);
	}
	private static List<CommandLineArgument> parse(String[] arguments, List<CommandLineOption> options) {
		List<CommandLineArgument> out = new ArrayList<>();

		for(CommandLineOption option : options) {
			List<String> values = null;
			for(int i = 0 ; i < arguments.length ; i++) {
				String nameArgument = arguments[i];
				if(!StringUtils.startsWith(nameArgument, "-")) {
					continue;
				}
				if(StringUtils.isNotBlank(option.getLongName())) {
					if(StringUtils.equals(nameArgument, "--" + option.getLongName())) {
						if(!option.isHasValue()) {
							values = createListIfNull(values);
							continue;
						}
						if(arguments.length < i+1) {
							values = createListIfNull(values);
							i+=2;
							continue;
						}
						values = createListIfNull(values);
						for(int j = i+1 ; j < arguments.length ; j++) {
							if(!StringUtils.startsWith(arguments[j], "-")) {
								values.add(arguments[j]);
								i++;
								continue;
							}
							break;
						}
						continue;
					}
					if(option.isHasValue() && StringUtils.startsWith(nameArgument, "--" + option.getLongName() + "=")) {
						values = createListIfNull(values);
						values.add(StringUtils.substringAfter(arguments[i], "="));
						continue;
					}
				}
				if(option.getShortName() != null) {
					if(StringUtils.equals(nameArgument, "-" + option.getShortName())) {
						if(!option.isHasValue()) {
							values = createListIfNull(values);
							continue;
						}
						if(arguments.length < i+1) {
							values = createListIfNull(values);
							i+=2;
							continue;
						}
						values = createListIfNull(values);
						for(int j = i+1 ; j < arguments.length ; j++) {
							if(!StringUtils.startsWith(arguments[j], "-")) {
								values.add(arguments[j]);
								i++;
								continue;
							}
							break;
						}
						continue;
					}
					if(option.isHasValue() && StringUtils.startsWith(nameArgument, "-" + option.getShortName() + "=")) {
						values = createListIfNull(values);
						values.add(StringUtils.substringAfter(arguments[i], "="));
						continue;
					}
					if(option.isHasValue() && StringUtils.startsWith(nameArgument, "-" + option.getShortName())) {
						values = createListIfNull(values);
						values.add(StringUtils.substringAfter(arguments[i], "-" + option.getShortName()));
						continue;
					}
				}
			}
			if(values != null) {
				out.add(new CommandLineArgument(option, values));
			}
		}
		return out;
	}
	private static List<String> createListIfNull(List<String> input){
		if(input == null) {
			return new ArrayList<>();
		}
		return input;
	}
}
