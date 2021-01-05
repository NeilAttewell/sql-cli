package sqlclient.cli.domain;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Neil Attewell
 */
public class Query {
	private final List<String> parts;
	private final String delimiter;
	public Query(List<String> parts, String delimiter) {
		this.parts = parts;
		this.delimiter = delimiter;
	}
	public List<String> getParts() {
		return parts;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public String getQuery() {
		return this.parts.stream().collect(Collectors.joining(" "));
	}
	@Override
	public String toString() {
		return "Query [parts=" + parts + ", delimiter=" + delimiter + "]";
	}

//	public String getLastPart() {
//		if(this.parts.isEmpty()) {
//			return null;
//		}
//		return this.parts.get(this.parts.size()-1);
//	}
//	public boolean isLastPartComplete(SpecialCharacterRegistry registry) {
//		String part = getLastPart();
//		if(part == null) {
//			return true;
//		}
//		for(String wrapper : registry.getWrapperStrings()) {
//			if(!StringUtils.startsWith(part, wrapper)) {
//				continue;
//			}
//			return StringUtils.endsWith(part, wrapper);
//		}
//		return true;
//	}
}
