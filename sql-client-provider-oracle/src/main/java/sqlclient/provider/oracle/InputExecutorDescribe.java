package sqlclient.provider.oracle;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.core.contracts.IQueryResult;
import sqlclient.core.domain.Query;
import sqlclient.core.executors.AbstractPatternCommandExecutor;
import sqlclient.provider.oracle.QueryResultSetMetadata.Column;

/**
 * @author Neil Attewell
 */
@Component
public class InputExecutorDescribe extends AbstractPatternCommandExecutor{
	@Autowired private Connection connection;

	public InputExecutorDescribe() {
		super();
		addPattern(Pattern.compile("^desc\\s+((?<schema>[a-zA-Z_]\\w*)\\.)?(?<tableName>[a-zA-Z_]\\w*)$", Pattern.CASE_INSENSITIVE), this::doDescribe);
	}
	@Override
	public String getCommand() {
		return "desc";
	}

	private IQueryResult doDescribe(Query query, Matcher matcher) throws SQLException {
		DatabaseMetaData metaData = this.connection.getMetaData();
		String schema = StringUtils.defaultIfBlank(matcher.group("schema"), this.connection.getSchema());
		ResultSet columnResultSet = metaData.getColumns(this.connection.getCatalog(), schema, matcher.group("tableName").toUpperCase(), null);

		List<QueryResultSetMetadata.Column> columns = new ArrayList<>();
		while(columnResultSet.next()) {
			Column column = new Column();
			column.setField(columnResultSet.getString("COLUMN_NAME"));
			column.setType(columnResultSet.getString("TYPE_NAME"));
			column.setSize(columnResultSet.getInt("COLUMN_SIZE"));
			column.setOrder(columnResultSet.getInt("ORDINAL_POSITION"));
			column.setNullable(columnResultSet.getBoolean("NULLABLE"));
			column.setAutoIncrement(columnResultSet.getString("IS_AUTOINCREMENT"));
			column.setDecimalSize(columnResultSet.getInt("DECIMAL_DIGITS"));
			columns.add(column);
		}
		Collections.sort(columns, (i1,i2)-> i1.getOrder().compareTo(i2.getOrder()));

		if(columns.isEmpty()) {
			throw new SQLException("Table does not exist");
		}

		Statement statement = this.connection.createStatement();
		ResultSet defaultValueResultSet = statement.executeQuery("Select COLUMN_NAME, DATA_DEFAULT from USER_TAB_COLUMNS where TABLE_NAME ='"+matcher.group("tableName").toUpperCase()+"'");
		while(defaultValueResultSet.next()) {
			String columnName = defaultValueResultSet.getString("COLUMN_NAME");
			String defaultValue = defaultValueResultSet.getString("DATA_DEFAULT");
			
			columns.stream()
			.filter(item -> StringUtils.equals(item.getField(), columnName))
			.forEach(item -> item.setDefaultValue(defaultValue));
		}
		
		return new QueryResultSetMetadata(columns);
	}
}
