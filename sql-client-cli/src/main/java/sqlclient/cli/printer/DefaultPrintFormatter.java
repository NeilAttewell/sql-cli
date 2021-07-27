package sqlclient.cli.printer;

import java.sql.SQLException;

import org.springframework.stereotype.Component;

import sqlclient.core.contracts.IPrintFormatter;
import sqlclient.core.contracts.IQueryResult;

@Component
public class DefaultPrintFormatter implements IPrintFormatter{

	@Override
	public boolean isDefault() {
		return true;
	}
	@Override
	public boolean canFormat(IQueryResult resultSet, int index) {
		return true;
	}
	@Override
	public String format(IQueryResult resultSet, int index, String nullValue) {
		try {
			Object data = resultSet.getObject(index);
			if(data == null) {
				return nullValue;
			}
			return "" + data;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
