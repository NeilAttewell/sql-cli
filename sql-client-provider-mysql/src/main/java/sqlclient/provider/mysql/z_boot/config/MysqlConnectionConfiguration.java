package sqlclient.provider.mysql.z_boot.config;

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

import sqlclient.core.ApplicationState;
import sqlclient.core.contracts.IOutputSink;
import sqlclient.core.util.cli.CommandLine;
import sqlclient.core.util.cli.CommandLineBuilder;
import sqlclient.core.util.cli.CommandLineOption;

/**
 * @author Neil Attewell
 */
@Configuration
@Profile("mysql_db")
@ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
public class MysqlConnectionConfiguration {
	@Autowired private CommandLine commandLine;
	@Autowired private IOutputSink sink;
	@Autowired private ApplicationState state;

	@Bean
	public String dbType() {
		return "MySQL";
	}
	@Bean
	public Connection mysqlConnection() throws ClassNotFoundException, SQLException {
		var hostname = this.commandLine.getValue("hostname","localhost");
		if(StringUtils.isBlank(hostname)) {
			throw new IllegalArgumentException("Missing hostname");
		} 
		var database = this.commandLine.getValue("db");
		
		var port = this.commandLine.getValue("port","3306");
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

		var autoCommit = this.commandLine.getValue("auto-commit");
		if(StringUtils.isBlank(autoCommit)) {
			autoCommit = "true";
		}
		this.state.setAutoCommit(Boolean.parseBoolean(autoCommit));

		long startTime=System.currentTimeMillis();
		Properties properties = new Properties();
		properties.put("user", username);
		properties.put("password", password);
		Connection connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + (StringUtils.isBlank(database) ? "" : ("/" + database)),properties);
		connection.setAutoCommit(this.state.isAutoCommit());
		
		this.sink.printInfo("Connection to MySQL established.  Took: " + (System.currentTimeMillis()-startTime) + ".  Auto Commit is " + (this.state.isAutoCommit() ? "Enabled" : "Disabled"));
		
		if(this.commandLine.getValue("db") != null) {
			this.state.setInputPromptPrefix(this.commandLine.getValue("db"));
		}
		return connection;
	}
	
	public static void addCommandLineArguments(CommandLineBuilder builder){
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			builder.addOptionGroup()
			.withName("Mysql DB")
			.withOption(new CommandLineOption('h', "hostname", "Database hostname", true, true))
			.withOption(new CommandLineOption('d', "db", "Database name", true, true))
			.withOption(new CommandLineOption('P', "port", "Database listener port", true, true))
			.withOption(new CommandLineOption('u', "username", "Username", true, true))
			.withOption(new CommandLineOption('p', "password", "Password", true, true))
			.withOption(new CommandLineOption('a', "auto-commit", "Auto Commit.  Default: true", true, true))
			.withConditionOption(new CommandLineOption('t', "type", "Database type",true, true))
			.withConditionValue("mysql")
			.build();
		} catch (ClassNotFoundException e) {
		}
	}
}
