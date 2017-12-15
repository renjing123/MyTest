package cc.joymaker.weiop.open.base.tablestore;

import java.util.LinkedHashMap;
import java.util.List;

import com.alicloud.openservices.tablestore.model.filter.CompositeColumnValueFilter;

public interface TableStoreDAO<T> {

	T save(T o) throws TableStoreException;
	
	T update(T o) throws TableStoreException;

	void delete(T o) throws TableStoreException;

	T get(LinkedHashMap<String, Object> primaryKeys) throws TableStoreException;

	List<T> getRange(LinkedHashMap<String, Object> beginPrimaryKeys, LinkedHashMap<String, Object> endPrimaryKeys,
			CompositeColumnValueFilter filter, int limit) throws TableStoreException;
	
	List<T> getRange(LinkedHashMap<String, Object> beginPrimaryKeys, LinkedHashMap<String, Object> endPrimaryKeys,
			CompositeColumnValueFilter filter) throws TableStoreException;
	
	List<T> getRange(LinkedHashMap<String, Object> beginPrimaryKeys, LinkedHashMap<String, Object> endPrimaryKeys) throws TableStoreException;
}
