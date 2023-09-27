package sqlclient.core;

import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class SpecialCharacterRegistry {
	private String[] wrapperStrings = new String[] {"\"","'"};
	private char escapeCharacter = '\\';
	private String primaryDelimiter = ";";
	private String cancelDelimiter = "\\c";
	private String[] secondartDelimiters = new String[] {"\\G","\\j"};

	public String[] getWrapperStrings() {
		return wrapperStrings;
	}
	public char getEscapeCharacter() {
		return escapeCharacter;
	}
	public String getPrimaryDelimiter() {
		return primaryDelimiter;
	}
	public void setPrimaryDelimiter(String primaryDelimiter) {
		this.primaryDelimiter = primaryDelimiter;
	}
	public String getCancelDelimiter() {
		return cancelDelimiter;
	}
	public void setCancelDelimiter(String cancelDelimiter) {
		this.cancelDelimiter = cancelDelimiter;
	}
	public String[] getEnabledDelimiters() {
		String[] out = new String[this.secondartDelimiters.length+2];
		out[0] = this.primaryDelimiter;
		out[1] = this.cancelDelimiter;
		for(int i=0;i<this.secondartDelimiters.length;i++) {
			out[i+2] = this.secondartDelimiters[i];
		}
		return out;
	}
}