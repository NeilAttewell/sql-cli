package sqlclient.cli.domain;

import org.apache.commons.lang.StringUtils;

/**
 * @author Neil Attewell
 */
public enum DisplayTypeEnum {
	Table(true, ";"),
	PivotTable(false, "\\g"),
	Json(false, "\\j");
	
	private final boolean isDefault;
	private final String delimiter;
	private DisplayTypeEnum(boolean isDefault, String delimiter) {
		this.isDefault=isDefault;
		this.delimiter=delimiter;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public static DisplayTypeEnum parse(String input) {
		for(DisplayTypeEnum item : values()) {
			if(StringUtils.equals(input, item.getDelimiter())) {
				return item;
			}
		}
		for(DisplayTypeEnum item : values()) {
			if(item.isDefault()) {
				return item;
			}
		}
		throw new IllegalArgumentException();
	}
}
