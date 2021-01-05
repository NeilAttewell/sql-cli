package sqlclient.cli.contracts;

import java.sql.SQLException;

/**
 * @author Neil Attewell
 */
public interface IQueryResult {

	int getColumnCount() throws SQLException;
	String getColumnName(int index) throws SQLException;
	int getColumnDisplaySize(int index) throws SQLException;

	boolean next() throws SQLException;
	Object getObject(int index) throws SQLException;

}
