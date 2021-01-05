package sqlclient.cli.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import sqlclient.cli.contracts.IQueryResult;

/**
 * @author Neil Attewell
 */
public class QueryResultSet implements IQueryResult{
	private final ResultSet payload;
	public QueryResultSet(ResultSet payload) {
		this.payload=payload;
	}
	@Override
	public int getColumnCount() throws SQLException {
		return this.payload.getMetaData().getColumnCount(); 
	}
	@Override
	public String getColumnName(int index) throws SQLException {
		return this.payload.getMetaData().getColumnName(index);
	}
	@Override
	public int getColumnDisplaySize(int index) throws SQLException {
		return this.payload.getMetaData().getColumnDisplaySize(index);
	}
	
	@Override
	public boolean next() throws SQLException {
		return this.payload.next();
	}
	@Override
	public Object getObject(int index) throws SQLException {
		return this.payload.getObject(index);
	}
}
