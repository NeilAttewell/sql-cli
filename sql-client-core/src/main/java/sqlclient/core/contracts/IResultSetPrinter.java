package sqlclient.core.contracts;

import java.sql.SQLException;

import sqlclient.core.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
public interface IResultSetPrinter {
	boolean canPrintForResultSet(DisplayTypeEnum displayType);
	boolean isDefaultForResultSet();
	void print(IQueryResult resultSet, long queryTime) throws SQLException;
}
