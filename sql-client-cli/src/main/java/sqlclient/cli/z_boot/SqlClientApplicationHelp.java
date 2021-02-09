package sqlclient.cli.z_boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;

import sqlclient.cli.z_boot.util.cli.CommandLine;

@SpringBootApplication
@EnableConfigurationProperties
public class SqlClientApplicationHelp implements CommandLineRunner{
	@Autowired private BuildProperties buildProperties;
	private CommandLine commandLine;
	
	public void init() {
		this.commandLine=SqlClientApplication.commandLine;
	}

	@Override
	public void run(String... args) throws Exception {
		this.commandLine.printHelp("Version: " + this.buildProperties.getVersion());
		this.commandLine.printHelp("Written by Neil Attewell");
	}
}
