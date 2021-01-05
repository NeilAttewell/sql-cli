package sqlclient.cli.printer;

import sqlclient.cli.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
public interface IResultUpdatePrinter {
	boolean canPrintForUpdate(DisplayTypeEnum displayType);
	boolean isDefaultForUpdate();
	void print(int updateCount, long queryTime);
}
