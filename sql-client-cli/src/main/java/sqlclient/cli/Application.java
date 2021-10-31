package sqlclient.cli;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.cli.contracts.IInputSource;
import sqlclient.core.QueryAliasRegistry;
import sqlclient.core.SpecialCharacterRegistry;
import sqlclient.core.contracts.IInputExecutor;
import sqlclient.core.contracts.IOutputSink;
import sqlclient.core.domain.Query;
import sqlclient.core.exceptions.ExitException;

/**
 * @author Neil Attewell
 */
@Component
public class Application {
	@Autowired private IInputSource inputSource;
	@Autowired private IOutputSink outputSink;
	@Autowired private List<? extends IInputExecutor> executors;
	@Autowired private SpecialCharacterRegistry characterRegistry;
	@Autowired private QueryAliasRegistry aliasRegistry;
	
	public void run() throws IOException {
		Query query;
		while((query = this.inputSource.read()) != null) {
			try {
				if(StringUtils.equals(this.characterRegistry.getCancelDelimiter(), query.getDelimiter())) {
					continue;
				}
				if(query.isPartOfMultiQuery()) {
					this.outputSink.writeLine("");
					this.outputSink.writeLine(query.getQuery().replaceAll("(?:\\n|\\r)", " "));
				}
				for(Query item : expand(query)) {
					findExecutor(item).execute(item);
				}
			}catch (SQLException e) {
				this.outputSink.writeLine("Query failed.  Message:["+e.getMessage()+"]");
			}catch (ExitException e) {
				return;
			}
		}
	}
	private List<Query> expand(Query query){
		List<Query> aliases = this.aliasRegistry.getAlias(query.getQuery(), query.getDelimiter());
		if(aliases == null) {
			return List.of(query);
		}
		return aliases.stream()
				.map(this::expand)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	
	
	
	private IInputExecutor findExecutor(Query query) {
		IInputExecutor out = this.executors.stream()
				.filter(item -> !item.isDefault())
				.filter(item -> item.canExecute(query))
				.findFirst()
				.orElse(null);
		if(out != null) {
			return out;
		}
		return this.executors.stream()
				.filter(item -> item.isDefault())
				.findFirst()
				.get();
	}
}
