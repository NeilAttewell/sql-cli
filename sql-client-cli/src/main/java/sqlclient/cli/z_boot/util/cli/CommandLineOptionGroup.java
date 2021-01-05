package sqlclient.cli.z_boot.util.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Neil Attewell
 */
public class CommandLineOptionGroup {
	private final String name;
	private final List<CommandLineOption> options;
	private final CommandLineOption conditionOption;
	private final String conditionValue;

	public CommandLineOptionGroup(String name, List<CommandLineOption> options, CommandLineOption conditionOption, String conditionValue) {
		this.name=name;
		this.options=options;
		this.conditionOption=conditionOption;
		this.conditionValue=conditionValue;
	}
	public String getName() {
		return name;
	}
	public List<CommandLineOption> getOptions() {
		return options;
	}
	public String getConditionValue() {
		return conditionValue;
	}
	public CommandLineOption getConditionOption() {
		return conditionOption;
	}


	public static class OptionGroupBuilder{
		private final CommandLineBuilder commandLineBuilder;
		private String name;
		private List<CommandLineOption> options = new ArrayList<>();
		private CommandLineOption conditionOption;
		private String conditionValue;

		public OptionGroupBuilder(CommandLineBuilder commandLineBuilder) {
			this.commandLineBuilder=commandLineBuilder;
		}
		
		public OptionGroupBuilder withName(String name) {
			this.name = name;
			return this;
		}
		public OptionGroupBuilder withOptions(List<CommandLineOption> options) {
			this.options.addAll(options);
			return this;
		}
		public OptionGroupBuilder withOption(CommandLineOption option) {
			this.options.add(option);
			return this;
		}
		public OptionBuilder withOption() {
			return new OptionBuilder(this);
		}
		public OptionGroupBuilder withConditionOption(CommandLineOption conditionOption) {
			this.conditionOption = conditionOption;
			return this;
		}
		public OptionGroupBuilder withConditionValue(String conditionValue) {
			this.conditionValue = conditionValue;
			return this;
		}

		public CommandLineBuilder build() {
			if(this.name == null) {
				throw new IllegalArgumentException("Name is required");
			}
			if(this.options.isEmpty()) {
				throw new IllegalArgumentException("At least one option is required");
			}
			this.commandLineBuilder.addOptionGroup(new CommandLineOptionGroup(this.name, this.options, this.conditionOption, this.conditionValue));
			return this.commandLineBuilder;
		}
	}
	public static class OptionBuilder{
		private final OptionGroupBuilder builder;
		private Character shortName;
		private String longName;
		private String description;
		private Boolean hasValue;

		public OptionBuilder(OptionGroupBuilder builder) {
			this.builder=builder;
		}
		public OptionBuilder withShortName(Character shortName) {
			this.shortName = shortName;
			return this;
		}
		public OptionBuilder withLongName(String longName) {
			this.longName = longName;
			return this;
		}
		public OptionBuilder withDescription(String description) {
			this.description = description;
			return this;
		}
		public OptionBuilder withHasValue(Boolean hasValue) {
			this.hasValue = hasValue;
			return this;
		}
		public OptionGroupBuilder build() {
			if(this.shortName == null && this.longName == null) {
				throw new IllegalArgumentException("Either short name or long name is required");
			}
			if(this.hasValue == null) {
				throw new IllegalArgumentException("Has value is required");
			}
			if(this.description == null) {
				throw new IllegalArgumentException("Description is required");
			}
			this.builder.withOption(new CommandLineOption(this.shortName, this.longName, this.description, this.hasValue, true));
			return this.builder;
		}
	}
}