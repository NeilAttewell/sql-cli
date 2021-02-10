package sqlclient.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import sqlclient.cli.contracts.IVariableStore;
import sqlclient.cli.z_boot.util.cli.CommandLine;

@Component
public class VariableStoreUser implements IVariableStore{
	@Autowired private CommandLine commandLine;
	private Map<String, String> variables = new HashMap<>();

	@PostConstruct
	public void loadAliases() throws IOException {
		String fileName = this.commandLine.getValue("variables");
		if(StringUtils.isBlank(fileName)) {
			return;
		}

		File file = new File(fileName);
		if(!file.isFile()) {
			return;
		}
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		properties.entrySet().stream()
		.map(item -> Tuple.of(item.getKey(),item.getValue()))
		.filter(item -> item._1 != null && item._1 != null)
		.forEach(item->set(item._1.toString(), item._2.toString()));
	} 



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
