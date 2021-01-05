package sqlclient.cli.sources.console;

import java.time.Instant;

import org.jline.reader.impl.history.DefaultHistory;

/**
 * @author Neil Attewell
 */
public class QueryOnlyHistory extends DefaultHistory{
	private boolean allowAddFromLineReader=true;
	public boolean isAllowAddFromLineReader() {
		return allowAddFromLineReader;
	}
	public void setAllowAddFromLineReader(boolean allowAddFromLineReader) {
		this.allowAddFromLineReader = allowAddFromLineReader;
	}
	@Override
	public void add(Instant time, String line) {
		if(!this.allowAddFromLineReader) {
			return;
		}
		super.add(time, line);
	}
	public void logQuery(Instant time, String line) {
		super.add(time, line);
	}
}
