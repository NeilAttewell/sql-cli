package sqlclient.provider.oracle;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import oracle.sql.CLOB;
import sqlclient.core.contracts.IPrintFormatter;
import sqlclient.core.contracts.IQueryResult;

@Component
public class ClobPrintFormatter implements IPrintFormatter{

	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public boolean canFormat(IQueryResult resultSet, int index) {
		try {
			Object data = resultSet.getObject(index);
			if(data == null) {
				return false;
			}
			return (data instanceof CLOB);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	@Override
	public String format(IQueryResult resultSet, int index, String nullValue) {
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
