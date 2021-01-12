package sqlclient.cli.z_boot.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import sqlclient.cli.domain.InputModeEnum;
import sqlclient.cli.domain.OutputModeEnum;
import sqlclient.cli.sinks.OutputSinkConsole;
import sqlclient.cli.sinks.OutputSinkFile;
import sqlclient.cli.sinks.OutputSinkStandardOut;
import sqlclient.cli.sources.InputSourceConsole;
import sqlclient.cli.sources.InputSourceExecute;
import sqlclient.cli.sources.InputSourceScript;
import sqlclient.cli.sources.InputSourceStandardIn;
import sqlclient.cli.z_boot.util.cli.CommandLine;
import sqlclient.cli.z_boot.util.cli.CommandLineBuilder;
import sqlclient.cli.z_boot.util.cli.CommandLineOption;

/**
 * @author Neil Attewell
 */
@Configuration
public class SourceAndSinkConfiguration {
	@Bean
	@Profile("in_console")
	public InputSourceConsole inputSourceConsole() {
		return new InputSourceConsole();
	}
	@Bean
	@Profile("in_execute")
	public InputSourceExecute inputSourceExecute() {
		return new InputSourceExecute();
	}
	@Bean
	@Profile("in_script")
	public InputSourceScript inputSourceScript() {
		return new InputSourceScript();
	}
	@Bean
	@Profile("in_standard")
	public InputSourceStandardIn inputSourceStandardIn() {
		return new InputSourceStandardIn();
	}

	@Bean
	@Profile("out_console")
	public OutputSinkConsole outputSinkConsole() {
		return new OutputSinkConsole();
	}
	@Bean
	@Profile("out_file")
	public OutputSinkFile outputSinkFile() {
		return new OutputSinkFile();
	}
	@Bean
	@Profile("out_standard")
	public OutputSinkStandardOut outputSinkStandardOut() {
		return new OutputSinkStandardOut();
	}



	public static void addCommandLineArguments(CommandLineBuilder builder){
		builder
		.addOption(new CommandLineOption('i', null, "Read queries from standart in", false, true))
		.addOption(new CommandLineOption('s', "script", "Read queries from script file", true, true))
		.addOption(new CommandLineOption('e', "execute", "Read queries from argument", true, true))
		.addOption(new CommandLineOption('o', "output", "Send results to file or \"-\" for standard out", true, true));
	}

	public static InputModeEnum getInputMode(CommandLine commandLine) {
		if(commandLine.isFound('i')) {
			return InputModeEnum.StandardIn;
		}
		if(commandLine.isFound("script")) {
			return InputModeEnum.Script;
		}
		if(commandLine.isFound("execute")) {
			return InputModeEnum.Execute;
		}
		return InputModeEnum.ConsoleIn;
	}
	public static OutputModeEnum getOutputMode(CommandLine commandLine) {
		InputModeEnum inputMode = getInputMode(commandLine);
		if(inputMode == InputModeEnum.ConsoleIn) {
			return OutputModeEnum.ConsoleOut;
		}
		if(commandLine.isFound('o')) {
			if(StringUtils.equalsIgnoreCase(commandLine.getValue('o'), "-")) {
				return OutputModeEnum.StandardOut;
			}
			return OutputModeEnum.File;
		}
		return OutputModeEnum.StandardOut;
	}
}
