package sqlclient.cli.printer;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;

import oracle.sql.CLOB;
import sqlclient.cli.contracts.IQueryResult;
import sqlclient.cli.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
public interface IResultSetPrinter {
	boolean canPrintForResultSet(DisplayTypeEnum displayType);
	boolean isDefaultForResultSet();
	void print(IQueryResult resultSet, long queryTime) throws SQLException;
	
	

	default String getColumnObject(IQueryResult resultSet, int index, String nullValue) {
		try {
			Object data = resultSet.getObject(index);
			if(data == null) {
				return nullValue;
			}
			if(data instanceof CLOB) {
				return IOUtils.toString(((CLOB) data).getCharacterStream());
			}
			return "" + data;
		} catch (SQLException | IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
