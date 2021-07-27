package sqlclient.provider.oracle.z_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sqlclient.provider.oracle.ClobPrintFormatter;
import sqlclient.provider.oracle.InputExecutorDescribe;

@Configuration
public class OracleConfiguration {
	@Bean
	public ClobPrintFormatter clobPrintFormatter() {
		return new ClobPrintFormatter();
	}
	@Bean
	public InputExecutorDescribe executorDescribe() {
		return new InputExecutorDescribe();
	}
}
