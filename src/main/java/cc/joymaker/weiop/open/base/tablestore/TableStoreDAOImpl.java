package cc.joymaker.weiop.open.base.tablestore;

import cc.joymaker.weiop.open.base.utils.JsonUtils;
import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.filter.CompositeColumnValueFilter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.Map.Entry;

/**
 * tablestore.endPoint.public=https://weiop-online.cn-beijing.ots.aliyuncs.com
 * tablestore.endPoint=https://weiop-online.cn-beijing.ots-internal.aliyuncs.com
 * tablestore.accessKeyId=6C052jAHeYjSSZUq
 * tablestore.accessKeySecret=xr4t6uTDrgGoCCxHo2WAOzwyUo0PGR
 * tablestore.instanceName=weiop-online
 * 
 * @author zhangyuxin85@gmail.com
 *
 * @param <T>
 */
abstract public class TableStoreDAOImpl<T> extends AbstractTableStoreDAOImpl
		implements TableStoreDAO<T>, InitializingBean {

	@Value("#{cfg['scope']}")
	String scope;

	Logger log = LoggerFactory.getLogger("tablestore.logger");

	private Class<T> entityClass;

	@Value("#{cfg['tablestore.endPoint']}")
	protected String endPoint;
	@Value("#{cfg['tablestore.accessKeyId']}")
	protected String accessKeyId;
	@Value("#{cfg['tablestore.accessKeySecret']}")
	protected String accessKeySecret;
	@Value("#{cfg['tablestore.instanceName']}")
	protected String instanceName;

	private static SyncClient client;

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	private Map<String, String> pkColumnsMap = new TreeMap<>();
	private Map<String, String> columnsMap = new TreeMap<>();

	@SuppressWarnings("unchecked")
	public TableStoreDAOImpl() {
		ParameterizedType type = (ParameterizedType) (getClass().getGenericSuperclass());
		entityClass = (Class<T>) type.getActualTypeArguments()[0];

		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(TableStorePrimaryKey.class)) {
				TableStorePrimaryKey pk = field.getAnnotation(TableStorePrimaryKey.class);
				String value = pk.value();
				pkColumnsMap.put(value, field.getName());
			} else if (field.isAnnotationPresent(TableStoreColumn.class)) {
				TableStoreColumn column = field.getAnnotation(TableStoreColumn.class);
				String value = column.value();
				columnsMap.put(value, field.getName());
			}
		}

		log.info(JsonUtils.writeValueAsString(columnsMap));
	}

	protected void init() {
		// ClientConfiguration提供了很多配置项，以下只列举部分。
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		// 设置建立连接的超时时间。
		clientConfiguration.setConnectionTimeoutInMillisecond(5000);
		// 设置socket超时时间。
		clientConfiguration.setSocketTimeoutInMillisecond(5000);
		// 设置重试策略，若不设置，采用默认的重试策略。
		clientConfiguration.setRetryStrategy(new AlwaysRetryStrategy());
		client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName, clientConfiguration);
	}

	private Object getFieldValue(T o, Field f) throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException, InvocationTargetException {
		String name = f.getName();
		if (f.isAccessible()) {
			return f.get(o);
		} else {
			String getterName = "get" + (name.charAt(0) + "").toUpperCase() + name.substring(1);
			Method getter = o.getClass().getMethod(getterName);
			return getter.invoke(o);
		}
	}

	@Override
	public T save(T o) throws TableStoreException {

		if (o.getClass().isAnnotationPresent(TableStore.class)) {

			try {
				TableStore table = (TableStore) o.getClass().getAnnotation(TableStore.class);
				String tableName = table.value();
				boolean overwrite = table.overwrite();

				Field[] fields = o.getClass().getDeclaredFields();

				PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

				for (Field field : fields) {
					if (field.isAnnotationPresent(TableStorePrimaryKey.class)) {
						// 主键
						TableStorePrimaryKey anno = field.getAnnotation(TableStorePrimaryKey.class);
						String keyname = anno.value();
						if (!anno.autoIncr()) {
							// 非自增
							primaryKeyBuilder.addPrimaryKeyColumn(keyname, getPrimaryKeyValue(o, field));
						} else {
							// 自增
							primaryKeyBuilder.addPrimaryKeyColumn(keyname, PrimaryKeyValue.AUTO_INCRMENT);
						}
					}
				}

				PrimaryKey primaryKey = primaryKeyBuilder.build();

				RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);

				if (!overwrite) {
					// 不覆盖
					rowPutChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));
				}

				for (Field field : fields) {
					if (field.isAnnotationPresent(TableStoreColumn.class)) {
						// 附属
						TableStoreColumn anno = field.getAnnotation(TableStoreColumn.class);
						String columnName = anno.value();
						ColumnValue cv = getColumnValue(o, field);
						if (cv != null)
							rowPutChange.addColumn(columnName, cv);
					}
				}

				PutRowResponse resp = client.putRow(new PutRowRequest(rowPutChange));
				info("SAVE " + o.getClass().getSimpleName() + JsonUtils.formBean(o) + ";resp=" + resp.getRequestId()
						+ "|" + resp.getTraceId());
				return o;
			} catch (Exception ex) {
				log.error("SAVE " + o.getClass().getSimpleName() + JsonUtils.formBean(o) + ";msg=" + ex.getMessage());
				throw new TableStoreException(0, ex.getMessage(), ex);
			}
		} else {
			throw new IllegalArgumentException("Table store annotations on this Entity not found.");
		}
	}

	private void info(String msg) {
		if ("test".equals(scope)) {
			log.info(msg);
		}
	}

	@Override
	public void delete(T o) throws TableStoreException {
		if (o.getClass().isAnnotationPresent(TableStore.class)) {

			try {
				TableStore table = (TableStore) o.getClass().getAnnotation(TableStore.class);
				String tableName = table.value();

				Field[] fields = o.getClass().getDeclaredFields();

				PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

				for (Field field : fields) {
					if (field.isAnnotationPresent(TableStorePrimaryKey.class)) {
						// 主键
						TableStorePrimaryKey anno = field.getAnnotation(TableStorePrimaryKey.class);
						String keyname = anno.value();
						primaryKeyBuilder.addPrimaryKeyColumn(keyname, getPrimaryKeyValue(o, field));
					}
				}

				PrimaryKey primaryKey = primaryKeyBuilder.build();
				RowDeleteChange rowDeleteChange = new RowDeleteChange(tableName, primaryKey);
				DeleteRowResponse resp = client.deleteRow(new DeleteRowRequest(rowDeleteChange));
				info("DELETE " + o.getClass().getSimpleName() + JsonUtils.formBean(o) + ";resp=" + resp.getRequestId()
						+ "|" + resp.getTraceId());
			} catch (Exception ex) {
				log.warn("DELETE " + o.getClass().getSimpleName() + JsonUtils.formBean(o) + ";msg=" + ex.getMessage());
				throw new TableStoreException(0, ex.getMessage(), ex);
			}
		} else {
			throw new IllegalArgumentException("Table store annotations on this Entity not found.");
		}
	}

	@Override
	public T get(LinkedHashMap<String, Object> primaryKeys) throws TableStoreException {
		if (entityClass.isAnnotationPresent(TableStore.class)) {

			try {
				TableStore table = (TableStore) entityClass.getAnnotation(TableStore.class);
				String tableName = table.value();

				PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

				for (Entry<String, Object> entry : primaryKeys.entrySet()) {

					Field field = entityClass.getDeclaredField(entry.getKey());

					if (field != null && field.isAnnotationPresent(TableStorePrimaryKey.class)) {
						TableStorePrimaryKey pk = field.getAnnotation(TableStorePrimaryKey.class);
						String pkName = pk.value();

						primaryKeyBuilder.addPrimaryKeyColumn(pkName, getPrimaryKeyValue(entry.getValue()));
					} else {
						throw new TableStoreException(0, "Primary Key not exists." + entry.getKey());
					}
				}
				PrimaryKey primaryKey = primaryKeyBuilder.build();

				SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, primaryKey);
				criteria.setMaxVersions(1);
				GetRowRequest request = new GetRowRequest(criteria);
				GetRowResponse resp = client.getRow(request);
				Row row = resp.getRow();

				if (row != null) {
					return transformSingleResult(row);
				} else
					return null;
			} catch (Exception ex) {
				log.error("get err " + ex.getMessage(), ex);
				throw new TableStoreException(0, ex.getMessage(), ex);
			}
		} else {
			throw new IllegalArgumentException("Table store annotations on this Entity not found.");
		}
	}

	@Override
	public T update(T o) throws TableStoreException {
		if (o.getClass().isAnnotationPresent(TableStore.class)) {

			try {
				TableStore table = (TableStore) o.getClass().getAnnotation(TableStore.class);
				String tableName = table.value();

				Field[] fields = o.getClass().getDeclaredFields();

				PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

				for (Field field : fields) {
					if (field.isAnnotationPresent(TableStorePrimaryKey.class)) {
						// 主键
						TableStorePrimaryKey anno = field.getAnnotation(TableStorePrimaryKey.class);
						String keyname = anno.value();
						if (!anno.autoIncr()) {
							// 非自增
							primaryKeyBuilder.addPrimaryKeyColumn(keyname, getPrimaryKeyValue(o, field));
						} else {
							// 自增
							primaryKeyBuilder.addPrimaryKeyColumn(keyname, PrimaryKeyValue.AUTO_INCRMENT);
						}
					}
				}

				PrimaryKey primaryKey = primaryKeyBuilder.build();

				RowUpdateChange rowUpdateChange = new RowUpdateChange(tableName, primaryKey);
				rowUpdateChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_EXIST));

				for (Field field : fields) {
					if (field.isAnnotationPresent(TableStoreColumn.class)) {

						// 附属
						TableStoreColumn anno = field.getAnnotation(TableStoreColumn.class);
						String columnName = anno.value();
						ColumnValue cv = getColumnValue(o, field);
						if (cv == null) {
							continue;
						}
						rowUpdateChange.put(columnName, cv);
					}
				}

				// client.putRow(new PutRowRequest(rowPutChange));
				UpdateRowResponse resp = client.updateRow(new UpdateRowRequest(rowUpdateChange));
				info("UPDATE " + o.getClass().getSimpleName() + JsonUtils.formBean(o) + ";resp=" + resp.getRequestId()
						+ "|" + resp.getTraceId());
				return o;
			} catch (Exception ex) {

				log.warn("UPDATE " + o.getClass().getSimpleName() + JsonUtils.formBean(o) + ";msg=" + ex.getMessage());
				throw new TableStoreException(0, ex.getMessage(), ex);
			}
		} else {
			throw new IllegalArgumentException("Table store annotations on this Entity not found.");
		}
	}

	@Override
	public List<T> getRange(LinkedHashMap<String, Object> beginPrimaryKeys,
			LinkedHashMap<String, Object> endPrimaryKeys, CompositeColumnValueFilter filter, int limit)
			throws TableStoreException {
		if (entityClass.isAnnotationPresent(TableStore.class)) {

			try {
				TableStore table = (TableStore) entityClass.getAnnotation(TableStore.class);
				String tableName = table.value();

				RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(tableName);
				rangeRowQueryCriteria.setMaxVersions(1);

				PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

				// 设置起始主键
				for (Entry<String, Object> entry : beginPrimaryKeys.entrySet()) {

					Field field = entityClass.getDeclaredField(entry.getKey());

					if (field != null && field.isAnnotationPresent(TableStorePrimaryKey.class)) {
						TableStorePrimaryKey pk = field.getAnnotation(TableStorePrimaryKey.class);
						String pkName = pk.value();
						PrimaryKeyValue pkValue = getPrimaryKeyValue(entry.getValue());
						primaryKeyBuilder.addPrimaryKeyColumn(pkName, pkValue);
					} else {
						throw new TableStoreException(0, "Primary Key not exists." + entry.getKey());
					}
				}
				rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());

				// 设置结束主键
				primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
				for (Entry<String, Object> entry : endPrimaryKeys.entrySet()) {

					Field field = entityClass.getDeclaredField(entry.getKey());

					if (field != null && field.isAnnotationPresent(TableStorePrimaryKey.class)) {
						TableStorePrimaryKey pk = field.getAnnotation(TableStorePrimaryKey.class);
						String pkName = pk.value();
						PrimaryKeyValue pkValue = getPrimaryKeyValue(entry.getValue());
						primaryKeyBuilder.addPrimaryKeyColumn(pkName, pkValue);
					} else {
						throw new TableStoreException(0, "Primary Key not exists." + entry.getKey());
					}
				}
				rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
				rangeRowQueryCriteria.setDirection(Direction.BACKWARD);
				if (limit > 0) {
					rangeRowQueryCriteria.setLimit(limit);
				}

				// 设置过滤器
				if (filter != null)
					rangeRowQueryCriteria.setFilter(filter);

				RangeIteratorParameter param = new RangeIteratorParameter(rangeRowQueryCriteria);

				Iterator<Row> resp = client.createRangeIterator(param);

				List<T> results = new ArrayList<>();
				resp.forEachRemaining((row) -> {
					try {
						results.add(transformSingleResult(row));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				
				info("GETRANGE " + entityClass.getSimpleName() + ";" + results.size() + ";" + results.size());
				return results;
			} catch (Exception ex) {
				log.error("get_range err " + ex.getMessage(), ex);
				throw new TableStoreException(0, ex.getMessage(), ex);
			}
		} else {
			throw new IllegalArgumentException("Table store annotations on this Entity not found.");
		}
	}

	@Override
	public List<T> getRange(LinkedHashMap<String, Object> beginPrimaryKeys,
			LinkedHashMap<String, Object> endPrimaryKeys) throws TableStoreException {
		return getRange(beginPrimaryKeys, endPrimaryKeys, null);
	}

	@Override
	public List<T> getRange(LinkedHashMap<String, Object> beginPrimaryKeys,
			LinkedHashMap<String, Object> endPrimaryKeys, CompositeColumnValueFilter filter)
			throws TableStoreException {
		return getRange(beginPrimaryKeys, endPrimaryKeys, filter, 0);
	}

	/**
	 * 把row转化为一条记录
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	protected T transformSingleResult(Row row) throws Exception {
		T o = entityClass.newInstance(); // 需要有无参构造函数
		// 处理主键
		PrimaryKey pk = row.getPrimaryKey();
		PrimaryKeyColumn[] pkColumns = pk.getPrimaryKeyColumns();

		for (PrimaryKeyColumn pkColumn : pkColumns) {
			String name = pkColumn.getName();
			PrimaryKeyValue value = pkColumn.getValue();

			String fieldName = pkColumnsMap.get(name);
			Field field = entityClass.getDeclaredField(fieldName); // 得到字段名
			String setterName = "set" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);
			Method setter = entityClass.getDeclaredMethod(setterName, field.getType());
			setter.invoke(o, getPrimaryKeyColumnValue(field, value));
		}
		// 处理附加字段
		Column[] columns = row.getColumns();
		for (Column column : columns) {
			String name = column.getName();
			ColumnValue value = column.getValue();

			String fieldName = columnsMap.get(name);
			Field field = entityClass.getDeclaredField(fieldName);
			if (field != null) {
				String setterName = "set" + (field.getName().charAt(0) + "").toUpperCase()
						+ field.getName().substring(1);
				Method setter = entityClass.getDeclaredMethod(setterName, field.getType());
				setter.invoke(o, getColumnValue(field, value));
			}
		}

		if (o instanceof TableStoreDataAware) {
			TableStoreDataAware target = (TableStoreDataAware) o;
			target.setPrimaryKeyColumns(pkColumns);
			target.setColumns(columns);
		}

		return o;
	}

	/**
	 * 获取到主键值
	 * 
	 * @param f
	 * @param value
	 * @return
	 * @throws TableStoreException
	 */
	protected Object getPrimaryKeyColumnValue(Field f, PrimaryKeyValue value) throws TableStoreException {
		String simpleType = f.getType().getSimpleName();
		if (streq(simpleType, "Integer", "int")) {
			return new Integer((int) value.asLong());
		}
		if (streq(simpleType, "Long", "long")) {
			return value.asLong();
		}
		if (streq(simpleType, "Date")) {
			long d = value.asLong();
			return new Date(d);
		}
		if (streq(simpleType, "String")) {
			return value.asString();
		}
		throw new TableStoreException(0, "Invalid primary key type:" + simpleType);
	}

	/**
	 * 转换column的值
	 * 
	 * @param f
	 * @param value
	 * @return
	 * @throws TableStoreException
	 */
	protected Object getColumnValue(Field f, ColumnValue value) throws TableStoreException {
		String simpleType = f.getType().getSimpleName();

		if (streq(simpleType, "Long", "long")) {
			return new Long(value.asLong());
		} else if (streq(simpleType, "Date")) {
			long d = value.asLong();
			return new Date(d);
		} else if (streq(simpleType, "String")) {
			return value.asString();
		} else if (streq(simpleType, "Integer", "int")) {
			return new Integer((int) value.asLong());
		} else if (streq(simpleType, "Double", "double")) {
			return value.asDouble();
		} else if (streq(simpleType, "Float", "float")) {
			return new Double(value.asDouble()).floatValue();
		} else if (streq(simpleType, "boolean", "Boolean")) {
			return new Boolean(value.asBoolean());
		}
		throw new TableStoreException(0, "Invalid primary key type:" + simpleType);
	}

	protected PrimaryKeyValue getPrimaryKeyValue(Object value) throws Exception {

		if (value instanceof PrimaryKeyValue) {
			return (PrimaryKeyValue) value;
		}

		String simpleType = value.getClass().getSimpleName();
		if (streq(simpleType, "Integer", "Long", "int", "Integer")) {
			return PrimaryKeyValue.fromLong(((Number) value).longValue());
		} else if (simpleType.equals("String")) {
			return PrimaryKeyValue.fromString((String) value);
		} else if (simpleType.equals("Date")) {
			Date date = (Date) value;
			return PrimaryKeyValue.fromLong(date.getTime());
		}
		throw new IllegalArgumentException(
				"Invalid primary key Type.  Table store only support Integer|int|Long|long|String ");

	}

	protected PrimaryKeyValue getPrimaryKeyValue(T o, Field field) throws Exception {
		Object value = getFieldValue(o, field);
		String simpleType = field.getType().getSimpleName();
		if (streq(simpleType, "Integer", "Long", "int", "Integer", "long")) {
			return PrimaryKeyValue.fromLong(((Number) value).longValue());
		} else if (simpleType.equals("String")) {
			return PrimaryKeyValue.fromString((String) value);
		} else if (streq(simpleType, "Date")) {
			Date date = (Date) value;
			return PrimaryKeyValue.fromLong(date.getTime());
		}
		throw new IllegalArgumentException(
				"Invalid primary key Type.  Table store only support Integer|int|Long|long|String :"
						+ field.getType().getSimpleName() + ";" + value);

	}

	protected ColumnValue getColumnValue(T o, Field field) throws Exception {
		Object value = getFieldValue(o, field);

		if (value == null) {
			return null;
		}

		String simpleType = field.getType().getSimpleName();

		if (streq(simpleType, "Integer", "Long", "int", "long")) {
			return ColumnValue.fromLong(((Number) value).longValue());

		} else if (streq(simpleType, "String")) {
			return ColumnValue.fromString((String) value);

		} else if (streq(simpleType, "Boolean", "boolean")) {
			return ColumnValue.fromBoolean((Boolean) value);

		} else if (streq(simpleType, "Date")) {
			Date date = (Date) value;
			return ColumnValue.fromLong(date.getTime());
		} else if (streq(simpleType, "Double", "double", "Float", "float")) {
			Number d = (Number) value;
			return ColumnValue.fromDouble(d.doubleValue());
		}
		throw new IllegalArgumentException(
				"Invalid Column Type.  Table store only support Integer|int|Long|long|String|Date|Boolean|boolean|Float|Double ");
	}

	private boolean streq(String src, String... target) {
		for (String dst : target) {
			if (src.equals(dst)) {
				return true;
			}
		}
		return false;
	}

	public static void test() throws Exception {
		String endPoint = "http://weiop-online.cn-beijing.ots.aliyuncs.com";
		String instanceName = "weiop-online";
		String accessKeyId = "6C052jAHeYjSSZUq";
		String accessKeySecret = "xr4t6uTDrgGoCCxHo2WAOzwyUo0PGR";
		// ClientConfiguration提供了很多配置项，以下只列举部分。
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		// 设置建立连接的超时时间。
		clientConfiguration.setConnectionTimeoutInMillisecond(5000);
		// 设置socket超时时间。
		clientConfiguration.setSocketTimeoutInMillisecond(5000);
		// 设置重试策略，若不设置，采用默认的重试策略。
		clientConfiguration.setRetryStrategy(new AlwaysRetryStrategy());
		client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName, clientConfiguration);

		// 读取并检查key
		// List<String> lines = FileUtils.readLines(new
		// File("/Users/alexzhang/src/tobacco/code/scripts/boundry.txt"),
		// "utf-8");
		// List<String> diff = new ArrayList<>();
		// for (String line : lines) {
		// PrimaryKeyBuilder builder =
		// PrimaryKeyBuilder.createPrimaryKeyBuilder();
		// builder.addPrimaryKeyColumn("code",
		// PrimaryKeyValue.fromString(line));
		//
		// PrimaryKey primaryKey = builder.build();
		//
		// SingleRowQueryCriteria criteria = new
		// SingleRowQueryCriteria("ts_region", primaryKey);
		// criteria.setMaxVersions(1);
		// GetRowResponse resp = client.getRow(new GetRowRequest(criteria));
		// Row row = resp.getRow();
		// if (row == null) {
		// System.out.println(line);
		// diff.add(line);
		// }
		// }
		// FileUtils.writeLines(new
		// File("/Users/alexzhang/src/tobacco/code/scripts/boundry.diff.txt"),
		// "utf-8", diff);

		List<String> lines = FileUtils.readLines(new File("/Users/alexzhang/src/tobacco/code/scripts/region.ori.txt"),
				"utf-8");
		int i = 0;
		for (String line : lines) {
			String[] values = line.split(",");
			String code = values[0].trim();
			String parentCode = values[1].trim();
			if ("null".equals(parentCode)) {
				parentCode = null;
			}
			String name = values[2].trim();
			String shortName = values[3].trim();
			Double lng = Double.parseDouble(values[4].trim());
			Double lat = Double.parseDouble(values[5].trim());
			int level = Integer.parseInt(values[6].trim());
			int sort = Integer.parseInt(values[7].trim());
			int status = Integer.parseInt(values[8].trim());

			PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
			primaryKeyBuilder.addPrimaryKeyColumn("code", PrimaryKeyValue.fromString(code));
			PrimaryKey primaryKey = primaryKeyBuilder.build();
			RowPutChange rowPutChange = new RowPutChange("ts_region", primaryKey);

			rowPutChange.setCondition(new Condition(RowExistenceExpectation.IGNORE));
			if (parentCode != null) {
				rowPutChange.addColumn(new Column("parent_id", ColumnValue.fromString(parentCode)));
			}
			rowPutChange.addColumn(new Column("name", ColumnValue.fromString(name)));
			rowPutChange.addColumn(new Column("short_name", ColumnValue.fromString(shortName)));
			rowPutChange.addColumn(new Column("latitude", ColumnValue.fromDouble(lat)));
			rowPutChange.addColumn(new Column("longitude", ColumnValue.fromDouble(lng)));
			rowPutChange.addColumn(new Column("level", ColumnValue.fromLong(level)));
			rowPutChange.addColumn(new Column("sort", ColumnValue.fromLong(sort)));
			rowPutChange.addColumn(new Column("status", ColumnValue.fromLong(status)));
			PutRowResponse resp = client.putRow(new PutRowRequest(rowPutChange));
			System.out.println(code + "|" + name + ":" + resp.getRequestId() + "," + resp.getTraceId());
			String l = code + "|" + parentCode + "|" + name + "|" + shortName + "|" + lng + "|" + lat + "|" + level
					+ "|" + sort + "|" + status + "|" + (i++);
			System.out.println(l); //
		}

		// FileUtils.writeLines(new
		// File("/Users/alexzhang/src/tobacco/code/scripts/region133.txt"),
		// "utf-8", r);

	}
}
