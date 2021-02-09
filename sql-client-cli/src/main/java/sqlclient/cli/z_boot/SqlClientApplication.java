package sqlclient.cli.z_boot;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import sqlclient.cli.Application;
import sqlclient.cli.contracts.IOutputSink;
import sqlclient.cli.z_boot.config.ConnectionMysqlConfiguration;
import sqlclient.cli.z_boot.config.ConnectionOracleConfiguration;
import sqlclient.cli.z_boot.config.ConsoleConfiguration;
import sqlclient.cli.z_boot.config.ServiceConfiguration;
import sqlclient.cli.z_boot.config.SourceAndSinkConfiguration;
import sqlclient.cli.z_boot.util.cli.CommandLine;
import sqlclient.cli.z_boot.util.cli.CommandLineBuilder;
import sqlclient.cli.z_boot.util.cli.CommandLineOption;
import sqlclient.cli.z_boot.util.cli.CommandLineOptionGroup;

/**
 * @author Neil Attewell
 */
@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackageClasses= {SourceAndSinkConfiguration.class})
public class SqlClientApplication implements CommandLineRunner{
	private static CommandLine commandLine;
	private static LocalTime startTime;

	@Autowired private Application application;
	@Autowired private IOutputSink outputSink;
	
	@Bean
	public CommandLine commandLine() {
		return SqlClientApplication.commandLine;
	}
	
	@Override
	public void run(String... args) throws Exception {
		long startupTime = ChronoUnit.MILLIS.between(this.startTime, LocalTime.now());
		this.outputSink.printInfo("Startup time: "+startupTime);
		this.outputSink.printInfo("");
		this.application.run();
	}

	public static void main(String[] args){
		try {
			SqlClientApplication.startTime = LocalTime.now();
			
			CommandLineBuilder builder = new CommandLineBuilder();
			builder.withArguments(args);
			builder.addArgument("-t=oracle");
			builder.addOption(new CommandLineOption(null, "debug", "Print debug info", false, false));
			builder.addHelpOption(new CommandLineOption(null, "help", "Prints this message", false, true));
			builder.addPropertiesOption(new CommandLineOption(null, "properties", "Load arguments from file/s", true, true));
			SourceAndSinkConfiguration.addCommandLineArguments(builder);
			ConsoleConfiguration.addCommandLineArguments(builder);
			ServiceConfiguration.addCommandLineArguments(builder);
			
			ConnectionOracleConfiguration.addCommandLineArguments(builder);
			ConnectionMysqlConfiguration.addCommandLineArguments(builder);


			CommandLine commandLine = builder.build();
			SqlClientApplication.commandLine=commandLine;
			
			if(commandLine.isFound("debug")) {
				System.out.println(commandLine);				
			}
			if(commandLine.isPrintHelp()) {
				SqlClientApplication.commandLine=commandLine;
				SpringApplication springApp = new SpringApplication(SqlClientApplicationHelp.class);
				springApp.setLogStartupInfo(false);
				springApp.run(args);
				return;
			}
			
			
			List<String> profiles = new ArrayList<>();
			profiles.add(SourceAndSinkConfiguration.getInputMode(commandLine).toProfile());
			profiles.add(SourceAndSinkConfiguration.getOutputMode(commandLine).toProfile());
			profiles.addAll(commandLine.getActiveGroups().stream()
					.map(CommandLineOptionGroup::getName)
					.map(item -> item.trim().toLowerCase().replaceAll("\\s+", "_"))
					.collect(Collectors.toList()));
			
			SpringApplication springApp = new SpringApplication(SqlClientApplication.class);
			springApp.setAdditionalProfiles(profiles.stream().filter(Objects::nonNull).collect(Collectors.toList()).toArray(new String[0]));
			springApp.setLogStartupInfo(false);
			springApp.run(args);
		}catch (Throwable e) {
			while(e.getCause() != null) {
				e = e.getCause();
			}
			System.err.println(e.getMessage());
		}
	}
}
