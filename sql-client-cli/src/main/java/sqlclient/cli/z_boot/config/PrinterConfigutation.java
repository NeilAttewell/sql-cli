package sqlclient.cli.z_boot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import sqlclient.cli.printer.IResultSetPrinter;

/**
 * @author Neil Attewell
 */
@Configuration
@ComponentScan(basePackageClasses = IResultSetPrinter.class)
public class PrinterConfigutation {
}
