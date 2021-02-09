package sqlclient.cli;

import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class ApplicationState {
	private boolean autoCommit;
	private int updateCount;
	private String inputPromptPrefix;
	private final VariableStoreUser variableStoreUser = new VariableStoreUser();
	private final VariableStoreSystem variableStoreSystem = new VariableStoreSystem();
	private final VariableStoreLastQueryResult variableStoreLastQueryResult= new VariableStoreLastQueryResult();
	
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
