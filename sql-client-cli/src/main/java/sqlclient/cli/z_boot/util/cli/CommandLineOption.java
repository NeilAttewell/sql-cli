package sqlclient.cli.z_boot.util.cli;

/**
 * @author Neil Attewell
 */
public class CommandLineOption{
	private final Character shortName;
	private final String longName;
	private final String description;
	private final boolean hasValue;
	private final boolean includeInHelp;

	public CommandLineOption(Character shortName, String longName, String description, boolean hasValue, boolean includeInHelp) {
		super();
		this.shortName = shortName;
		this.longName = longName;
		this.hasValue = hasValue;
		this.description=description;
		this.includeInHelp=includeInHelp;
	}
	public Character getShortName() {
		return shortName;
	}
	public String getLongName() {
		return longName;
	}
	public boolean isHasValue() {
		return hasValue;
	}
	public String getDescription() {
		return description;
	}
	public boolean isIncludeInHelp() {
		return includeInHelp;
	}
	@Override
	public String toString() {
		return "Option [shortName=" + shortName + ", longName=" + longName + ", hasValue=" + hasValue + ", description=" + description + "]";
	}


	
	
	public static class OptionBuilder{
		private final CommandLineBuilder commandLineBuilder;
		private Character shortName;
		private String longName;
		private String description;
		private Boolean hasValue;

		public OptionBuilder(CommandLineBuilder commandLineBuilder) {
			this.commandLineBuilder=commandLineBuilder;
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
		public CommandLineBuilder build() {
			if(this.shortName == null && this.longName == null) {
				throw new IllegalArgumentException("Either short name or long name is required");
			}
			if(this.hasValue == null) {
				throw new IllegalArgumentException("Has value is required");
			}
			this.commandLineBuilder.addOption(new CommandLineOption(this.shortName, this.longName, this.description, this.hasValue, true));
			return this.commandLineBuilder;
		}
	}
}