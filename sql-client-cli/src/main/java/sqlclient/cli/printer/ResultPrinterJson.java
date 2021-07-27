package sqlclient.cli.printer;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import net.sf.json.JSONObject;
import sqlclient.core.contracts.IOutputSink;
import sqlclient.core.contracts.IPrintFormatter;
import sqlclient.core.contracts.IQueryResult;
import sqlclient.core.contracts.IResultSetPrinter;
import sqlclient.core.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
@Component
public class ResultPrinterJson implements IResultSetPrinter{
	private static final String NULL_PLACEHOLDER="{{~NULL~PLACEHOLDER~}}";
	@Autowired private IOutputSink outputSink;
	@Autowired private List<? extends IPrintFormatter> formatters;

	@Override
	public boolean canPrintForResultSet(DisplayTypeEnum displayType) {
		return displayType == DisplayTypeEnum.Json;
	}
	@Override
	public boolean isDefaultForResultSet() {
		return false;
	}

	@Override
	public void print(IQueryResult resultSet, long queryTime) throws SQLException {
		List<Tuple2<Integer, String>> columns = IntStream.range(1, resultSet.getColumnCount() + 1)
		.mapToObj(item -> getColumnMetadata(resultSet, item))
		.collect(Collectors.toList());
		
		int count=0;
		this.outputSink.write("[");
		String seperator="";
		while (resultSet.next()) {
			count++;
			this.outputSink.write(seperator + "\n\t" + StringUtils.replace(buildRecord(columns, resultSet).toString(),  "\"" + NULL_PLACEHOLDER + "\"", "null"));
			seperator = ",";
		}
		this.outputSink.writeLine("\n]");
		this.outputSink.printInfo(count + " row in set ("+ queryTime +" ms)");
	}

	private Tuple2<Integer, String> getColumnMetadata(IQueryResult resultSet, int index) {
		try {
			String name = resultSet.getColumnName(index);
			return Tuple.of(index, name);
		} catch (SQLException e) {
			throw new IllegalArgumentException("invalid index: " + index, e);
		}
	}
	private JSONObject buildRecord(List<Tuple2<Integer, String>> columns, IQueryResult resultSet) {
		JSONObject out = new JSONObject();
		columns.forEach(column -> out.accumulate(column._2, IPrintFormatter.formatter(this.formatters, resultSet, column._1, NULL_PLACEHOLDER)));
		return out;
	}
}
