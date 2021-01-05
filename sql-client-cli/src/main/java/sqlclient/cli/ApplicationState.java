package sqlclient.cli;

import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class ApplicationState {
	private boolean autoCommit;
	private int updateCount;
	
	public boolean isAutoCommit() {
		return autoCommit;
	}
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	public int getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}
}
