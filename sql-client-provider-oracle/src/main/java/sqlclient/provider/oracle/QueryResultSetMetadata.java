package sqlclient.provider.oracle;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import sqlclient.core.contracts.IQueryResult;

/**
 * @author Neil Attewell
 */
public class QueryResultSetMetadata implements IQueryResult{
	private final List<Column> tableColumns;
	private int index=-1;
	
	public QueryResultSetMetadata(List<Column> tableColumns) throws SQLException {
		this.tableColumns=tableColumns;
	}
	
	@Override
	public String getColumnName(int index) throws SQLException {
		switch (index) {
		case 1: return "Field";
		case 2: return "Type";
		case 3: return "Default";
		case 4: return "Null";
		}
		return null;
	}
	@Override
	public String getObject(int index) throws SQLException {
		switch (index) {
		case 1: {
			return this.tableColumns.get(this.index).getField();
		}
		case 2: {
			var type = this.tableColumns.get(this.index).getType();
			var size = this.tableColumns.get(this.index).getSize();
			var decimalSize = this.tableColumns.get(this.index).getDecimalSize();
			if(size == 0 && decimalSize == 0) {
				return type;
			}
			if(StringUtils.equalsIgnoreCase(type, "VARCHAR2")) {
				return type + "(" + size + " BYTE)";
			}
			if(StringUtils.startsWith(type, "TIMESTAMP")) {
				return type;
			}
			if(decimalSize <= 0) {
				return type + "(" + size + ")";
			}
			return type + "(" + size + (decimalSize != null ? (","+decimalSize) : "") + ")";
		}
		case 3:{
			return this.tableColumns.get(this.index).getDefaultValue();
		}
		case 4: {
			var nullable = this.tableColumns.get(this.index).getNullable();
			if(nullable == null) {
				return "";
			}
			if(nullable == true) {
				return "NULL";
			}
			return "";
		}
		}
		return null;
	}
	
	
	@Override
	public int getColumnCount() throws SQLException {
		return 4;
	}
	@Override
	public int getColumnDisplaySize(int index) throws SQLException {
		return Math.max(StringUtils.length(getColumnName(index)), StringUtils.length(getObject(index)));
	}
	@Override
	public boolean next() throws SQLException {
		this.index++;
		return this.index < this.tableColumns.size();
	}
	

	public boolean hasData() {
		return !this.tableColumns.isEmpty();
	}
	
	public static class Column{
		private String field;
		private String type;
		private Integer size;
		private Integer decimalSize;
		private Integer order;
		private Boolean nullable;
		private String autoIncrement;
		private String defaultValue;
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Integer getSize() {
			return size;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		public Integer getOrder() {
			return order;
		}
		public void setOrder(Integer order) {
			this.order = order;
		}
		public Boolean getNullable() {
			return nullable;
		}
		public void setNullable(Boolean nullable) {
			this.nullable = nullable;
		}
		public String getAutoIncrement() {
			return autoIncrement;
		}
		public void setAutoIncrement(String autoIncrement) {
			this.autoIncrement = autoIncrement;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		public Integer getDecimalSize() {
			return decimalSize;
		}
		public void setDecimalSize(Integer decimalSize) {
			this.decimalSize = decimalSize;
		}
	}
}
