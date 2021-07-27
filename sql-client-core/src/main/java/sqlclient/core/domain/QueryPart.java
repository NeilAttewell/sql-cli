package sqlclient.core.domain;

public class QueryPart{
	private final String value;
	private final boolean wrapped;
	public QueryPart(String value, boolean wrapped) {
		this.value=value;
		this.wrapped=wrapped;
	}
	public String getValue() {
		return value;
	}
	public boolean isWrapped() {
		return wrapped;
	}
	@Override
	public String toString() {
		return "QueryPart [value=" + value + ", wrapped=" + wrapped + "]";
	}
}