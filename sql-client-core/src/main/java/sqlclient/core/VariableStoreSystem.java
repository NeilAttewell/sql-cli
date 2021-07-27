package sqlclient.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import io.vavr.Tuple2;

@Component
public class VariableStoreSystem {
	private Map<String, Tuple2<Supplier<String>, Consumer<String>>> functions;

	public VariableStoreSystem() {
		Map<String, Tuple2<Supplier<String>, Consumer<String>>> functions = new HashMap<>();
		this.functions=functions;
	}
	
	public String get(String name) {
		return getFunction(name)._1.get();
	}
	public void set(String name, String value) {
		getFunction(name)._2.accept(value);
	}
	private Tuple2<Supplier<String>, Consumer<String>> getFunction(String name){
		name = StringUtils.trimToEmpty(StringUtils.upperCase(name));
		var tuple = this.functions.get(name.toUpperCase());
		if(tuple != null) {
			return tuple;
		}
		throw new IllegalArgumentException("System variable not found with name:[" + name + "]");
	}

}
