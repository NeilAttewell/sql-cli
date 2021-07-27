package sqlclient.cli.sources;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.core.ApplicationState;

/**
 * @author Neil Attewell
 */
@Component
public class InputSourceConsole extends AbstractInputSource{
	@Autowired private LineReader lineReader;
	@Autowired private ApplicationState state;
	@Autowired @Qualifier("dbType")
	private String dbType;
	public InputSourceConsole() {
	}
	@PostConstruct
	public void init() {
	}
	@Override
	public Tuple2<String, Boolean> readLine() throws IOException {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(StringUtils.defaultIfBlank(this.state.getInputPromptPrefix(), this.dbType + " SQL"));
			builder.append(" ");
			if(!this.state.isAutoCommit()) {
				builder.append(StringUtils.rightPad(" #" + this.state.getUpdateCount(), 5, " "));
			}
			builder.append(this.state.isAutoCommit() ? StringUtils.rightPad(" #" + this.state.getUpdateCount(), 5, " ") : "");
			builder.append("> ");
			
			return Tuple.of(this.lineReader.readLine(builder.toString()), true);
		}catch (EndOfFileException|UserInterruptException e) {
			return null;
		}
	}
	public void destroy() throws Exception {
	}
}
