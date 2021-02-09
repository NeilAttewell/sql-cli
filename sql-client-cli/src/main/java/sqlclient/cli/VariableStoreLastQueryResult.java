package sqlclient.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import sqlclient.cli.contracts.IVariableStore;

public class VariableStoreLastQueryResult implements IVariableStore{
	private Map<String, String> variables = new HashMap<>();

	@Override
	public void clearAll() {
		this.variables.clear();
	}
	@Override
	public void set(String name,String value) {
		name = StringUtils.upperCase(StringUtils.trimToEmpty(name));
		this.variables.put(name, StringUtils.trimToEmpty(value));
	}
	@Override
	public String get(String name) {
		name = StringUtils.upperCase(StringUtils.trimToEmpty(name));
		return this.variables.get(name);
	}
	@Override
	public List<String> getNames(){
		return this.variables.keySet().stream()
				.sorted()
				.collect(Collectors.toList()); 
	}
}
