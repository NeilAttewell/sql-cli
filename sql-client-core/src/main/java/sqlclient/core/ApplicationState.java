package sqlclient.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Neil Attewell
 */
@Component
public class ApplicationState {
	@Autowired private VariableStoreUser variableStoreUser;
	@Autowired private VariableStoreLastQueryResult variableStoreLastQueryResult;
	private boolean autoCommit;
	private String inputPromptPrefix;
	
	public boolean isAutoCommit() {
		return autoCommit;
	}
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	public String getInputPromptPrefix() {
		return inputPromptPrefix;
	}
	public void setInputPromptPrefix(String inputPromptPrefix) {
		this.inputPromptPrefix = inputPromptPrefix;
	}
	public VariableStoreUser getVariableStoreUser() {
		return variableStoreUser;
	}
	public VariableStoreLastQueryResult getVariableStoreLastQueryResult() {
		return variableStoreLastQueryResult;
	}
}
