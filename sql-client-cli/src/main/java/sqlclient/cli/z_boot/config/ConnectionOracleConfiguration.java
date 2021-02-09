package sqlclient.cli.z_boot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import sqlclient.cli.QueryAliasRegistry;
import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.contracts.IOutputSink;
import sqlclient.cli.z_boot.util.cli.CommandLine;
import sqlclient.cli.z_boot.util.cli.CommandLineBuilder;
import sqlclient.cli.z_boot.util.cli.CommandLineOption;

/**
 * @author Neil Attewell
 */
@Configuration
@Profile("oracle_db")
@ConditionalOnClass(name = "oracle.jdbc.driver.OracleDriver")
public class ConnectionOracleConfiguration {
	@Autowired private CommandLine commandLine;
	@Autowired private IOutputSink sink;
	@Autowired private QueryAliasRegistry aliasRegistry;
	@Autowired private SpecialCharacterRegistry characterRegistry;

	@Bean
	public String dbType() {
		return "Oracle";
	}
	@Bean
	public Connection oracleConnection() throws ClassNotFoundException, SQLException {
		var hostname = this.commandLine.getValue("hostname");
		if(StringUtils.isBlank(hostname)) {
			throw new IllegalArgumentException("Missing hostname");
		} 
		var sid = this.commandLine.getValue("sid");
		if(StringUtils.isBlank(sid)) {
			throw new IllegalArgumentException("Missing SID");
		}
		var port = this.commandLine.getValue("port");
		if(StringUtils.isBlank(port)) {
			throw new IllegalArgumentException("Missing port");
		}
		var username = this.commandLine.getValue("username");
		if(StringUtils.isBlank(username)) {
			throw new IllegalArgumentException("Missing username");
		}
		var password = this.commandLine.getValue("password");
		if(StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("Missing password");
		}

		//		Class.forName("oracle.jdbc.driver.OracleDriver");
		Properties properties = new Properties();
		properties.put("user", username);
		properties.put("password", password);
		long startTime=System.currentTimeMillis();
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@" + hostname + ":" + port + "/" + sid,properties);
		connection.setAutoCommit(false);
		this.sink.printInfo("Connection to Oracle established.  Took: " + (System.currentTimeMillis()-startTime));

		this.aliasRegistry.addFixedAlias(this.characterRegistry, "show tables", "select table_name from user_tables order by 1");
		return connection;
	}
	public static void addCommandLineArguments(CommandLineBuilder builder){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			builder.addOptionGroup()
			.withName("Oracle DB")
			.withOption(new CommandLineOption('h', "hostname", "Database hostname", true, true))
			.withOption(new CommandLineOption('S', "sid", "System ID", true, true))
			.withOption(new CommandLineOption('P', "port", "Database listener port", true, true))
			.withOption(new CommandLineOption('u', "username", "Username", true, true))
			.withOption(new CommandLineOption('p', "password", "Password", true, true))
			.withConditionOption(new CommandLineOption('t', "type", "Database type",true, true))
			.withConditionValue("oracle")
			.build();
		} catch (ClassNotFoundException e) {
		}
	}
}
