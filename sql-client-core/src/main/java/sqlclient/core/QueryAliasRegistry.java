package sqlclient.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.core.domain.Query;
import sqlclient.core.util.SqlParserUtils;
import sqlclient.core.util.cli.CommandLine;

/**
 * @author Neil Attewell
 */
@Component
public class QueryAliasRegistry {

	@Autowired private CommandLine commandLine;
	@Autowired private SpecialCharacterRegistry characterRegistry;
	private Map<String, Tuple2<String, List<Query>>> aliasMapInitial = new HashMap<>();
	private Map<String, Tuple2<String, List<Query>>> aliasMapLoaded = new HashMap<>();
	private Map<String, Tuple2<String, List<Query>>> aliasMapSession = new HashMap<>();

	@PostConstruct
	public void loadAliases() throws IOException {
		loadAliasesFromFile("~/.sql-client/alias-default.txt");
		loadAliasesFromFile(this.commandLine.getValue("alias"));
	}
	private void loadAliasesFromFile(String fileName) throws IOException{
		if(StringUtils.isBlank(fileName)) {
			return;
		}

		File file = new File(fileName);
		if(!file.isFile()) {
			return;
		}
		var map = new HashMap<String, List<String>>();

		List<String> currentAliasLines=null;
		for(String line : FileUtils.readLines(file,Charset.defaultCharset())) {
			String tmp = StringUtils.trim(line);
			if(StringUtils.startsWith(tmp, "[") && StringUtils.endsWith(tmp, "]")) {
				tmp = StringUtils.substringAfter(tmp, "[");
				tmp = StringUtils.substringBeforeLast(tmp, "]");
				currentAliasLines = new ArrayList<>();
				map.put(tmp.toLowerCase(), currentAliasLines);
				continue;
			}
			if(currentAliasLines == null) {
				continue;
			}
			currentAliasLines.add(line);
		}

		map.entrySet().stream()
				.filter(item -> !item.getValue().isEmpty())
				.map(item -> Tuple.of(item.getKey(), StringUtils.join(item.getValue(), " ")))
				.forEach(item -> addAlias(this.aliasMapLoaded, this.characterRegistry, item._1, item._2));
	}

	private void addAlias(Map<String, Tuple2<String, List<Query>>> map, SpecialCharacterRegistry characterRegistry, String alias, String queryString){
		map.put(alias.toLowerCase(), Tuple.of(alias, SqlParserUtils.parse(queryString, characterRegistry)));
	}

	public void addInitialAlias(SpecialCharacterRegistry characterRegistry, String alias, String queryString) {
		addAlias(this.aliasMapInitial, this.characterRegistry, alias, queryString);
	}

	public List<String> getAliasNames(){
		return Stream.of(this.aliasMapInitial, this.aliasMapLoaded, this.aliasMapSession)
				.flatMap(item -> item.keySet().stream())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}
	public List<Query> getAlias(String input, String delimiter) {
		List<Query> queries = getAlias(input);
		if(queries == null) {
			return null;
		}
		if(delimiter == null) {
			return queries;
		}
		return queries.stream()
				.map(item -> {
					if(StringUtils.isNotBlank(item.getDelimiter())) {
						return item;
					}
					return new Query(item.getParts(), delimiter, false);
				})
				.collect(Collectors.toList());
	}
	private List<Query> getAlias(String input) {
		input = StringUtils.trimToEmpty(StringUtils.lowerCase(input));

		var alias = this.aliasMapSession.get(input);
		if(alias != null) {
			return alias._2;
		}
		alias = this.aliasMapLoaded.get(input);
		if(alias != null) {
			return alias._2;
		}
		alias = this.aliasMapInitial.get(input);
		if(alias != null) {
			return alias._2;
		}
		return null;
	}

	public void setAlias(String name, String queryString) {
		addAlias(this.aliasMapSession, this.characterRegistry, name, queryString);
	}

	public void removeAlias(String name) {
		this.aliasMapSession.remove(name.toLowerCase());
	}
}
