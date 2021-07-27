package sqlclient.provider.mysql.z_boot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import sqlclient.provider.mysql.z_boot.config.MysqlConnectionConfiguration;

@Configuration
@Profile("mysql_db")
@ComponentScan(basePackageClasses = MysqlConnectionConfiguration.class)
public class MysqlConfiguration {

}
