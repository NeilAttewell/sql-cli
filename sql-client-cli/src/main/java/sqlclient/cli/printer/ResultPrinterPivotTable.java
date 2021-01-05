package sqlclient.cli.printer;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import sqlclient.cli.contracts.IOutputSink;
import sqlclient.cli.contracts.IQueryResult;
import sqlclient.cli.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
@Component
public class ResultPrinterPivotTable implements IResultSetPrinter{
	@Autowired private IOutputSink outputSink;
	@Override
	public boolean canPrintForResultSet(DisplayTypeEnum displayType) {
		return displayType == DisplayTypeEnum.PivotTable;
	}
	@Override
	public boolean isDefaultForResultSet() {
		return false;
	}

	@Override
	public void print(IQueryResult resultSet, long queryTime) throws SQLException {
		List<Tuple3<Integer, String, Integer>> columns = IntStream.range(1, resultSet.getColumnCount() + 1)
		.mapToObj(item -> getColumnMetadata(resultSet, item))
		.collect(Collectors.toList());

		int recordNumber=0;
		while (resultSet.next()) {
			this.outputSink.writeLine(buildLine(++recordNumber));
			columns.forEach(column -> {
				this.outputSink.writeLine(buildRecordLine(column, getColumnObject(resultSet, column._1, "NULL")));
			});
		}
		this.outputSink.printInfo(recordNumber + " row in set ("+ queryTime +" ms)");
	}
	private String buildLine(int count) {
		int length = 62;
		String line = " " + count + ". row ";
		line = StringUtils.leftPad(line, length / 2, "*");
		return StringUtils.rightPad(line, length, "*");
	}
	private Tuple3<Integer, String, Integer> getColumnMetadata(IQueryResult resultSet, int index) {
		try {
			String name = resultSet.getColumnName(index);
			return Tuple.of(index, name, name.length());
		} catch (SQLException e) {
			throw new IllegalArgumentException("invalid index: " + index, e);
		}
	}
	public String buildRecordLine(Tuple3<Integer, String, Integer> column, String value) {
		return StringUtils.leftPad(" " + column._2, column._3, " ") + " : " + value;
	}
}
