package sqlclient.cli.printer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import sqlclient.cli.contracts.IOutputSink;
import sqlclient.cli.contracts.IQueryResult;
import sqlclient.cli.domain.DisplayTypeEnum;

/**
 * @author Neil Attewell
 */
@Component
public class ResultPrinterTable implements IResultSetPrinter{
	@Autowired private IOutputSink outputSink;
	@Override
	public boolean canPrintForResultSet(DisplayTypeEnum displayType) {
		return displayType == DisplayTypeEnum.Table;
	}
	@Override
	public boolean isDefaultForResultSet() {
		return true;
	}
	@Override
	public void print(IQueryResult resultSet, long queryTime) throws SQLException {
		Map<Integer, Integer> columnWidths = new HashMap<>();

		List<Tuple2<Integer,String>> columnNames = IntStream.range(1, resultSet.getColumnCount() + 1)
				.mapToObj(item -> {
					String name = expand(getColumnName(resultSet, item));
					columnWidths.put(item, name.length());
					return Tuple.of(item,name);
				})
				.collect(Collectors.toList());


		List<Map<Integer, String>> records = new ArrayList<>();
		while (resultSet.next()) {
			records.add(columnNames.stream()
					.map(item -> {
						String value = expand(getColumnObject(resultSet, item._1, "NULL"));
						columnWidths.put(item._1, Math.max(columnWidths.get(item._1), value.length()));
						return Tuple.of(item._1, value);
					})
					.collect(Collectors.toMap(Tuple2::_1, Tuple2::_2)));
		}

		this.outputSink.writeLine(buildLine(columnWidths));
		this.outputSink.writeLine(buildHeader(columnNames, columnWidths));
		this.outputSink.writeLine(buildLine(columnWidths));
		records.forEach(line -> this.outputSink.writeLine(buildRecord(columnWidths, line)));
		this.outputSink.writeLine(buildLine(columnWidths));
		this.outputSink.printInfo(records.size() + " row in set ("+ queryTime +" ms)");
		this.outputSink.printInfo("");
		
	}

	private String buildLine(Map<Integer, Integer> columns) {
		StringBuilder out = new StringBuilder();
		columns.forEach((k,v) -> {
			out.append("+" + StringUtils.rightPad("", v+2,"-"));
		});
		out.append("+");
		return out.toString();
	}
	private String buildHeader(List<Tuple2<Integer,String>> columns, Map<Integer, Integer> columnWidths) {
		StringBuilder out = new StringBuilder();
		columns.forEach(item -> {
			out.append("| " + StringUtils.rightPad(item._2, columnWidths.get(item._1)+1));
		});
		out.append("|");
		return out.toString();
	}
	private String buildRecord(Map<Integer, Integer> columnWidths, Map<Integer, String> data) {
		StringBuilder out = new StringBuilder();
		IntStream.range(1, columnWidths.size()+1).forEach(index->{
			out.append("| " + StringUtils.rightPad(data.get(index), columnWidths.get(index)+1));
		});
		out.append("|");
		return out.toString();
	}
	private String getColumnName(IQueryResult resultSet, int index) {
		try {
			return resultSet.getColumnName(index);
		} catch (SQLException e) {
			throw new IllegalArgumentException("invalid index: " + index, e);
		}
	}
	private static String expand(String input) {
		if(StringUtils.isBlank(input)) {
			return input;
		}
		if(!StringUtils.contains(input, "\t")) {
			return input;
		}
		
		final int tabSize=4;
	    StringBuilder out = new StringBuilder();
	    int col = 0;
	    for (int i = 0; i < input.length(); i++) {
	        char c = input.charAt(i);
	        switch (c) {
	            case '\n' :
	                col = 0;
	                out.append(c);
	                break;
	            case '\t' :
	                out.append(StringUtils.leftPad("",tabSize - col % tabSize));
	                col += tabSize - col % tabSize;
	                break;
	            default :
	                col++;
	                out.append(c);
	                break;
	        }
	    }
	    return out.toString();
	}
}
