package sqlclient.cli.domain;

/**
 * @author Neil Attewell
 */
public enum OutputModeEnum {
	StandardOut("out_standard"),
	File("out_file"),
	ConsoleOut("out_console");
	private final String profile;
	private OutputModeEnum(String profile) {
		this.profile=profile;
	}
	public String toProfile() {
		return profile;
	}
}
