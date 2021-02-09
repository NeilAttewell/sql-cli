package sqlclient.cli.contracts;

import java.util.List;

public interface IVariableStore {
	public void clearAll();
	public void set(String name,String value);
	public String get(String name);
	public List<String> getNames();
}
