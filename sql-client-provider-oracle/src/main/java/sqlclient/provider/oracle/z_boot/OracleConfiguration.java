package sqlclient.provider.oracle.z_boot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import sqlclient.provider.oracle.z_boot.config.OracleConnectionConfiguration;

@Configuration
@Profile("oracle_db")
@ComponentScan(basePackageClasses = OracleConnectionConfiguration.class)
public class OracleConfiguration {

}
