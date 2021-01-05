package sqlclient.cli.z_boot.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import sqlclient.cli.sources.console.MultilineParser;
import sqlclient.cli.sources.console.QueryOnlyHistory;
import sqlclient.cli.z_boot.util.cli.CommandLine;
import sqlclient.cli.z_boot.util.cli.CommandLineBuilder;
import sqlclient.cli.z_boot.util.cli.CommandLineOption;


/**
 * @author Neil Attewell
 */
@Configuration
public class ConsoleConfiguration {
	@Autowired private CommandLine commandLine;
	
	@Bean
	@Profile("in_console")
	public MultilineParser consoleParser() {
		MultilineParser parser = new MultilineParser();
		parser.setEscapeChars(new char[0]);
		return parser;
	}
	@Bean
	@Profile("in_console")
	public LineReader consoleLineReader() throws IOException {
		Terminal terminal = TerminalBuilder.builder()
				.system(true)
				.signalHandler(Terminal.SignalHandler.SIG_IGN)
				.build();

		QueryOnlyHistory history = new QueryOnlyHistory();
		history.setAllowAddFromLineReader(true);
		
		LineReaderBuilder builder = LineReaderBuilder.builder()
				.terminal(terminal)
				.history(history)
				.parser(consoleParser());
		
		
		String historyFile = this.commandLine.getValue("history-file");
		if(StringUtils.isNotBlank(historyFile)) {
			File file = new File(historyFile);
			if(!file.exists()) {
				file.createNewFile();
			}
			if(file.exists()) {
				builder.variable(LineReader.HISTORY_FILE, historyFile);
			}
		}
		builder.option(org.jline.reader.LineReader.Option.AUTO_MENU, true);
		
		return builder.build();
	}
	public static void addCommandLineArguments(CommandLineBuilder builder){
		builder.addOption(new CommandLineOption('H', "history-file", "Location of the history file", true, true));
	}
}
