package cc.joymaker.weiop.open.base.tablestore;

import java.util.LinkedHashMap;

public class TableStoreObject {

	private LinkedHashMap<String, Object> primaryKeys;

	private LinkedHashMap<String, Object> columns;

	public LinkedHashMap<String, Object> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(LinkedHashMap<String, Object> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public LinkedHashMap<String, Object> getColumns() {
		return columns;
	}

	public void setColumns(LinkedHashMap<String, Object> columns) {
		this.columns = columns;
	}

}
