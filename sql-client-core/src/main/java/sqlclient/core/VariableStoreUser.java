package sqlclient.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import io.vavr.Tuple2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import sqlclient.core.contracts.IVariableStore;
import sqlclient.core.domain.Query;
import sqlclient.core.util.cli.CommandLine;

@Component
public class VariableStoreUser implements IVariableStore{
	@Autowired private CommandLine commandLine;
	private Map<String, String> variableMapInitial = new HashMap<>();
	private Map<String, String> variableMapLoaded = new HashMap<>();
	private Map<String, String> variableMapSession = new HashMap<>();

	@PostConstruct
	public void loadVariables() throws IOException {
		loadVariablesFromFile("~/.sql-client/variables-default.txt");
		loadVariablesFromFile(this.commandLine.getValue("variables"));
	}
	private void loadVariablesFromFile(String fileName) throws IOException {
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
		.filter(item -> item._1 != null && item._2 != null)
		.forEach(item->set(this.variableMapLoaded,item._1.toString(), item._2.toString()));
	} 
	private void set(Map<String, String> variableMap, String name, String value){
		name = StringUtils.upperCase(StringUtils.trimToEmpty(name));
		variableMap.put(name, StringUtils.trimToEmpty(value));
	}


	@Override
	public void clearAll() {
		this.variableMapSession.clear();
	}
	@Override
	public void set(String name,String value) {
		set(this.variableMapSession, name, value);
	}
	@Override
	public void remove(String name) {
		name = StringUtils.upperCase(StringUtils.trimToEmpty(name));
		this.variableMapSession.remove(name);
	}
	@Override
	public String get(String name) {
		name = StringUtils.upperCase(StringUtils.trimToEmpty(name));
		var out = this.variableMapSession.get(name);
		if(StringUtils.isNotBlank(out)) {
			return out;
		}
		out = this.variableMapLoaded.get(name);
		if(StringUtils.isNotBlank(out)) {
			return out;
		}
		return StringUtils.trimToNull(this.variableMapInitial.get(name));
	}
	@Override
	public List<String> getNames(){
		return Stream.of(this.variableMapInitial, this.variableMapLoaded, this.variableMapSession)
				.flatMap(item -> item.keySet().stream())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}
}
