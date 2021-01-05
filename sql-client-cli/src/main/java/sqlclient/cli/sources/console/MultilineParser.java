package sqlclient.cli.sources.console;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import sqlclient.cli.QueryAliasRegistry;
import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.contracts.ICommand;
import sqlclient.cli.domain.Query;
import sqlclient.cli.z_boot.util.SqlParserUtils;

/**
 * @author Neil Attewell
 */
public class MultilineParser extends DefaultParser{
	@Autowired private SpecialCharacterRegistry characterRegistry;
	@Autowired private QueryAliasRegistry aliasRegistry;
	@Autowired @Lazy 
	private List<? extends ICommand> commands;
	
	@Override
	public ParsedLine parse(String line, int cursor, ParseContext context) {
		if(StringUtils.isBlank(line)) {
			return super.parse(line, cursor, context);
		}
		List<Query> queries = SqlParserUtils.parse(line, this.characterRegistry);
		Query query = queries.get(queries.size()-1);
		
		if(StringUtils.isNotBlank(query.getDelimiter())) {
			return super.parse(line, cursor, context);
		}
		var alias = this.aliasRegistry.getAlias(query.getQuery(), query.getDelimiter());
		if(alias != null) {
			return super.parse(line, cursor, context);
		}
		if(this.commands != null && this.commands.stream().filter(item -> item.isCommand(query)).count() > 0) {
			return super.parse(line, cursor, context);
		}
		
		String[] lines = line.split("\n");
		throw new EOFError(lines.length-1, lines[lines.length-1].length()-1, "Missing terminator");
	}
}
