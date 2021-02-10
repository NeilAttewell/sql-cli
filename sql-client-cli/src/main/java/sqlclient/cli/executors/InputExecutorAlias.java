package sqlclient.cli.executors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple1;
import sqlclient.cli.QueryAliasRegistry;
import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.domain.Query;
import sqlclient.cli.domain.TupleListQueryResult;

@Component
public class InputExecutorAlias extends AbstractPatternCommandExecutor{
	@Autowired private SpecialCharacterRegistry characterRegistry;
	@Autowired private QueryAliasRegistry aliasRegistry;

	public InputExecutorAlias() {
		super();
		addPattern(Pattern.compile("^alias$", Pattern.CASE_INSENSITIVE), this::doList);
		addPattern(Pattern.compile("^alias\\s+(?<name>([a-zA-Z_]\\s*[_a-zA-Z0-9]*)+)$", Pattern.CASE_INSENSITIVE), this::doView);
	}
	@Override
	public String getCommand() {
		return "alias";
	}

	private TupleListQueryResult<Tuple1<String>> doList(Query query, Matcher matcher) throws SQLException {
		List<Tuple1<String>> rows = this.aliasRegistry.getAliasNames().stream()
				.map(item -> Tuple.of(item))
				.collect(Collectors.toList());
		return new TupleListQueryResult<>(Tuple.of("name"), rows);
	}

	private TupleListQueryResult<Tuple1<String>> doView(Query input, Matcher matcher) throws SQLException {
		String name = matcher.group("name");
		var query = this.aliasRegistry.getAlias(name, this.characterRegistry.getPrimaryDelimiter());
		if(query == null) {
			query = new ArrayList<>();
		}
		var rows = query.stream()
				.map(item -> item.getQuery() + StringUtils.defaultIfBlank(item.getDelimiter(), this.characterRegistry.getPrimaryDelimiter()))
				.map(item -> Tuple.of(item))
				.collect(Collectors.toList());

		return new TupleListQueryResult<Tuple1<String>>(Tuple.of(name), rows);
	}
}