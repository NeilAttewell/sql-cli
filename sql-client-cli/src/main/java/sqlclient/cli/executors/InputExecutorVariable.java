package sqlclient.cli.executors;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple2;
import sqlclient.cli.domain.TupleListQueryResult;
import sqlclient.core.ApplicationState;
import sqlclient.core.domain.Query;
import sqlclient.core.executors.AbstractPatternCommandExecutor;

/**
 * @author Neil Attewell
 * 
 * List variables:	variable
 * View variable:	variable some variable
 * Set variables:	variable some variable = some value
 */
@Component
public class InputExecutorVariable extends AbstractPatternCommandExecutor{
	@Autowired private ApplicationState applicationState;

	public InputExecutorVariable() {
		super();
		addPattern(Pattern.compile("^variable$", Pattern.CASE_INSENSITIVE), this::doList);
		addPattern(Pattern.compile("^variable\\s+(?<variableName>[a-zA-Z_]\\w*)$", Pattern.CASE_INSENSITIVE), this::doView);
		addPattern(Pattern.compile("^variable\\s+(?<variableName>[a-zA-Z_]\\w*)\\s*=\\s*(?<variableValue>.+)$", Pattern.CASE_INSENSITIVE), this::doSet);
	}
	@Override
	public String getCommand() {
		return "variable";
	}

	private TupleListQueryResult<Tuple2<String,String>> doList(Query query, Matcher matcher) throws SQLException {
		return new TupleListQueryResult<>(Tuple.of("name","value"), this.applicationState.getVariableStoreUser().getNames().stream()
				.map(item -> Tuple.of(item.toUpperCase(), this.applicationState.getVariableStoreUser().get(item)))
				.collect(Collectors.toList()));
	}

	private TupleListQueryResult<Tuple2<String,String>> doView(Query input, Matcher matcher) throws SQLException {
		String name = matcher.group("variableName");
		var value = this.applicationState.getVariableStoreUser().get(name);
		if(value == null) {
			return new TupleListQueryResult<>(Tuple.of("name","value"), List.of());
		}
		return new TupleListQueryResult<>(Tuple.of("name","value"), List.of(Tuple.of(name.toUpperCase(),value)));
	}
	private TupleListQueryResult<Tuple0> doSet(Query input, Matcher matcher) throws SQLException {
		String name = matcher.group("variableName");
		String value = matcher.group("variableValue");
		this.applicationState.getVariableStoreUser().set(name, value);
		return null;
	}
}