package sqlclient.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class ApplicationState {
	@Autowired private VariableStoreUser variableStoreUser;
	@Autowired private VariableStoreSystem variableStoreSystem;
	@Autowired private VariableStoreLastQueryResult variableStoreLastQueryResult;
	private boolean autoCommit;
	private int updateCount;
	private String inputPromptPrefix;
	
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
	public String getInputPromptPrefix() {
		return inputPromptPrefix;
	}
	public void setInputPromptPrefix(String inputPromptPrefix) {
		this.inputPromptPrefix = inputPromptPrefix;
	}
	public VariableStoreSystem getVariableStoreSystem() {
		return variableStoreSystem;
	}
	public VariableStoreUser getVariableStoreUser() {
		return variableStoreUser;
	}
	public VariableStoreLastQueryResult getVariableStoreLastQueryResult() {
		return variableStoreLastQueryResult;
	}
}
