package sqlclient.core.contracts;

import java.util.List;

public interface IPrintFormatter {
	boolean isDefault();
	boolean canFormat(IQueryResult resultSet, int index);
	String format(IQueryResult resultSet, int index, String nullValue);


	static String formatter(List<? extends IPrintFormatter> formatters, IQueryResult resultSet, int index, String nullValue) {
		var out = formatters.stream()
				.filter(item -> !item.isDefault())
				.filter(item -> item.canFormat(resultSet, index))
				.findFirst()
				.orElse(null);
		if(out != null) {
			return out.format(resultSet, index, nullValue);
		}
		return formatters.stream()
				.filter(item -> item.isDefault())
				.findFirst()
				.map(item -> item.format(resultSet, index, nullValue))
				.orElse(nullValue);
	}
}
