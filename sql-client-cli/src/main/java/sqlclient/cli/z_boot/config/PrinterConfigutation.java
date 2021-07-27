package sqlclient.cli.z_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sqlclient.cli.printer.DefaultPrintFormatter;
import sqlclient.cli.printer.ResultPrinterJson;
import sqlclient.cli.printer.ResultPrinterPivotTable;
import sqlclient.cli.printer.ResultPrinterTable;
import sqlclient.cli.printer.ResultUpdatePrinter;

/**
 * @author Neil Attewell
 */
@Configuration
public class PrinterConfigutation {
	@Bean 
	public ResultPrinterJson resultPrinterJson(){
		return new ResultPrinterJson();
	}
	@Bean 
	public ResultPrinterPivotTable resultPrinterPivotTable(){
		return new ResultPrinterPivotTable();
	}
	@Bean 
	public ResultPrinterTable resultPrinterTable(){
		return new ResultPrinterTable();
	}
	@Bean 
	public ResultUpdatePrinter resultUpdatePrinter(){
		return new ResultUpdatePrinter();
	}
	@Bean 
	public DefaultPrintFormatter defaultPrintFormatter(){
		return new DefaultPrintFormatter();
	}
}
