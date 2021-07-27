package sqlclient.core.domain;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Neil Attewell
 */
public class Query {
	private final List<QueryPart> parts;
	private final String delimiter;
	public Query(List<QueryPart> parts, String delimiter) {
		this.parts = parts;
		this.delimiter = delimiter;
	}
	public List<QueryPart> getParts() {
		return parts;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public String getQuery() {
		return this.parts.stream().map(QueryPart::getValue).collect(Collectors.joining(" "));
	}
	@Override
	public String toString() {
		return "Query [parts=" + parts + ", delimiter=" + delimiter + "]";
	}
}
