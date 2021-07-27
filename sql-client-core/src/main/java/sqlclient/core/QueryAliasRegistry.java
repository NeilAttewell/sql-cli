package sqlclient.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private Map<String, Tuple2<String, List<Query>>> fixedAliases = new HashMap<>();
	private Map<String, Tuple2<String, List<Query>>> aliases = new HashMap<>();

	@PostConstruct
	public void loadAliases() throws IOException {
		String fileName = this.commandLine.getValue("alias");
		if(StringUtils.isBlank(fileName)) {
			return;
		}

		File file = new File(fileName);
		if(!file.isFile()) {
			return;
		}

		Map<String, List<String>> map = new HashMap<>();

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
		.forEach(item -> addFixedAlias(this.characterRegistry, item._1, item._2));
	} 

	public void addFixedAlias(SpecialCharacterRegistry characterRegistry, String alias, String queryString) {
		List<Query> queries = SqlParserUtils.parse(queryString, characterRegistry);
		this.fixedAliases.put(alias.toLowerCase(), Tuple.of(alias, queries));
	}

	public List<String> getAliasNames(){
		return Stream.of(this.fixedAliases, this.aliases)
				.map(item -> item.values())
				.flatMap(item -> item.stream())
				.map(item -> item._1)
				.distinct()
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
					return new Query(item.getParts(), delimiter);
				})
				.collect(Collectors.toList());
	}
	private List<Query> getAlias(String input) {
		input = StringUtils.trimToEmpty(StringUtils.lowerCase(input));
		var alias = this.aliases.get(input);
		if(alias != null) {
			return alias._2;
		}
		alias = this.fixedAliases.get(input);
		if(alias != null) {
			return alias._2;
		}
		return null;
	}

	public void setAlias(String name, String queryString) {
		List<Query> queries = SqlParserUtils.parse(queryString, this.characterRegistry);
		this.aliases.put(name.toLowerCase(), Tuple.of(name, queries));
	}

	public void removeAlias(String name) {
		this.aliases.remove(name.toLowerCase());
	}
}
