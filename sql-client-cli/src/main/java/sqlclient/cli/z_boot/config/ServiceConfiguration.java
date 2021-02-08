package sqlclient.cli.z_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sqlclient.cli.Application;
import sqlclient.cli.ApplicationState;
import sqlclient.cli.QueryAliasRegistry;
import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.z_boot.util.cli.CommandLineBuilder;
import sqlclient.cli.z_boot.util.cli.CommandLineOption;

/**
 * @author Neil Attewell
 */
@Configuration
public class ServiceConfiguration {
	@Bean
	public Application application() {
		return new Application();
	}
	@Bean
	public SpecialCharacterRegistry characterRegistry() {
		return new SpecialCharacterRegistry();
	}
	@Bean
	public QueryAliasRegistry queryAliasRegistry() {
		return new QueryAliasRegistry();
	}
	@Bean
	public ApplicationState applicationState() {
		return new ApplicationState();
	}

	public static void addCommandLineArguments(CommandLineBuilder builder){
		builder.addOption(new CommandLineOption('a', "alias", "Location of alias file.", true, true));
	}
}
