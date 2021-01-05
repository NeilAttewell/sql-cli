package sqlclient.cli.printer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.cli.contracts.IOutputSink;
import sqlclient.cli.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
@Component
public class ResultUpdatePrinter implements IResultUpdatePrinter{
	@Autowired private IOutputSink outputSink;

	@Override
	public boolean canPrintForUpdate(DisplayTypeEnum displayType) {
		return false;
	}

	@Override
	public boolean isDefaultForUpdate() {
		return true;
	}

	@Override
	public void print(int updateCount, long queryTime) {
		this.outputSink.printInfo("Total records updated: " + updateCount + " (" + queryTime + " ms)");
	}

}
