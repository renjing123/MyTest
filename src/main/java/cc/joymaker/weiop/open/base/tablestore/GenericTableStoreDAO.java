package cc.joymaker.weiop.open.base.tablestore;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 实现还没做，懒得做， 晚点做
 * @author zhangyuxin85@gmail.com
 *
 */
public interface GenericTableStoreDAO {

	
	void save(TableStoreObject obj) throws TableStoreException;

	
	void update(TableStoreObject obj) throws TableStoreException;

	
	void delete(TableStoreObject obj) throws TableStoreException;

	
	TableStoreObject get(LinkedHashMap<String, Object> primaryKeys) throws TableStoreException;

	
	List<TableStoreObject> getRange(LinkedHashMap<String, Object> beginPrimaryKeys,
			LinkedHashMap<String, Object> endPrimaryKeys) throws TableStoreException;
	
	
}
