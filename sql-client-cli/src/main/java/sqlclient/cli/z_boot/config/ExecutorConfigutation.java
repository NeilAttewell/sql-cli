package sqlclient.cli.z_boot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import sqlclient.cli.executors.AbstractCommandExecutor;

/**
 * @author Neil Attewell
 */
@Configuration
@ComponentScan(basePackageClasses = AbstractCommandExecutor.class)
public class ExecutorConfigutation {
}
