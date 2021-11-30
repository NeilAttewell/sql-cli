package sqlclient.cli.z_boot;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import sqlclient.cli.Application;
import sqlclient.cli.z_boot.config.ConsoleConfiguration;
import sqlclient.cli.z_boot.config.ServiceConfiguration;
import sqlclient.cli.z_boot.config.SourceAndSinkConfiguration;
import sqlclient.core.contracts.IOutputSink;
import sqlclient.core.util.cli.CommandLine;
import sqlclient.core.util.cli.CommandLineBuilder;
import sqlclient.core.util.cli.CommandLineOption;
import sqlclient.core.util.cli.CommandLineOptionGroup;
import sqlclient.provider.mysql.z_boot.config.MysqlConnectionConfiguration;
import sqlclient.provider.oracle.z_boot.config.OracleConnectionConfiguration;

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
	
	
	@Override
	public void run(String... args) throws Exception {
		long startupTime = ChronoUnit.MILLIS.between(this.startTime, LocalTime.now());
		this.outputSink.printInfo("Startup time: "+startupTime);
		this.outputSink.printInfo("");
		this.application.run();
	}
	public static CommandLine getCommandLine() {
		return SqlClientApplication.commandLine;
	}
	public static void main(String[] args){
		try {
			SqlClientApplication.startTime = LocalTime.now();
			
			CommandLineBuilder builder = new CommandLineBuilder();
			builder.withArguments(args);
			builder.addArgument("-t=oracle");
			builder.addOption(new CommandLineOption(null, "debug", "Print debug info", false, false));
			builder.addHelpOption(new CommandLineOption(null, "help", "Prints this message", true, true));
			builder.addPropertiesOption(new CommandLineOption(null, "properties", "Load arguments from file/s", true, true));
			SourceAndSinkConfiguration.addCommandLineArguments(builder);
			ConsoleConfiguration.addCommandLineArguments(builder);
			ServiceConfiguration.addCommandLineArguments(builder);
			
			OracleConnectionConfiguration.addCommandLineArguments(builder);
			MysqlConnectionConfiguration.addCommandLineArguments(builder);


			CommandLine commandLine = builder.build();
			SqlClientApplication.commandLine=commandLine;
			
			if(commandLine.isFound("debug")) {
				System.out.println(commandLine);				
			}
			if(commandLine.isPrintHelp()) {
				InputStream inputStream = SqlClientApplication.class.getClassLoader().getResourceAsStream("META-INF/build-info.properties");
				Properties sourceProperties = new Properties();
				sourceProperties.load(inputStream);
				inputStream.close();

				String prefix = "build.";
				Properties targetProperties = new Properties();
				for (String key : sourceProperties.stringPropertyNames()) {
					if (key.startsWith(prefix)) {
						targetProperties.put(key.substring(prefix.length()), sourceProperties.get(key));
					}
				}
				BuildProperties buildProperties = new BuildProperties(targetProperties);
				commandLine.printHelp("Version: " + buildProperties.getVersion() + 
						"\nhttps://github.com/NeilAttewell/sql-client" +
						"\nWritten by Neil Attewell");
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
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
}
