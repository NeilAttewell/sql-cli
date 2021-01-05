package sqlclient.cli.domain;

/**
 * @author Neil Attewell
 */
public enum InputModeEnum {
	ConsoleIn("in_console"),
	Script("in_script"),
	StandardIn("in_standard"),
	Execute("in_execute");
	private final String profile;
	private InputModeEnum(String profile) {
		this.profile=profile;
	}
	public String toProfile() {
		return profile;
	}
}
