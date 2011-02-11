package org.nutz.dao.entity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.castor.Castors;
import org.nutz.dao.Chain;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;

/**
 * 记录对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Record implements Map<String, Object>,java.io.Serializable {

	/**
	 * @author mawenming  at Jan 11, 2011 2:20:09 PM
	 */
	private static final long serialVersionUID = 4614645901639942051L;

	public static Record create(ResultSet rs) {
		try {
			Record re = new Record();
			ResultSetMetaData meta = rs.getMetaData();
			int count = meta.getColumnCount();
			for (int i = 1; i <= count; i++) {
				String name = meta.getColumnLabel(i);
				if (meta.getColumnType(i) == Types.CLOB) {
					re.set(name, rs.getString(i));
				} else {
					re.set(name, rs.getObject(i));
				}
			}
			return re;
		}
		catch (SQLException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private Map<String, Object> map;

	public Record() {
		map = new HashMap<String, Object>();
	}

	/**
	 * 设置值
	 * 
	 * @param name
	 *            字段名
	 * @param value
	 *            字段值
	 * @return 记录本身
	 */
	public Record set(String name, Object value) {
		map.put(name.toLowerCase(), value);
		return this;
	}

	/**
	 * 移除一个字段
	 * 
	 * @param name
	 *            字段名
	 * @return 移除的字段值
	 */
	public Object remove(String name) {
		return map.remove(name.toLowerCase());
	}

	/**
	 * @return 记录的字段数
	 */
	public int getColumnCount() {
		return map.size();
	}

	/**
	 * @return 记录的所有字段名
	 */
	public Set<String> getColumnNames() {
		return map.keySet();
	}

	public int getInt(String name) {
		return Castors.me().castTo(get(name), int.class);
	}

	public String getString(String name) {
		return Castors.me().castToString(get(name));
	}

	public Timestamp getTimestamp(String name) {
		return Castors.me().castTo(get(name), Timestamp.class);
	}

	public String toJson(JsonFormat format) {
		return Json.toJson(map, format);
	}

	public String toString() {
		return Json.toJson(map);
	}

	/**
	 * 如果你想将这个记录转换成你的数据库实体类，请用这个方法
	 * 
	 * @param enType
	 *            实体类型
	 * @return 实体的一个实例
	 * 
	 * @see org.nutz.dao.Dao#getEntity(Class)
	 */
	public <T> T toPojo(Entity<T> enType) {
		try {
			T obj = enType.getType().newInstance();
			for (EntityField ef : enType.fields()) {
				Object v = map.get(ef.getColumnName());
				if (null != v) {
					ef.setValue(obj, v);
				}
			}
			return obj;
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public <T> T toPojo(Class<T> type) {
		return Lang.map2Object(map, type);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object out) {
		return map.equals(out);
	}

	public Object get(Object name) {
		if (null == name)
			return null;
		return map.get(name.toString().toLowerCase());
	}

	public int hashCode() {
		return map.hashCode();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Object put(String name, Object value) {
		return map.put(name.toLowerCase(), value);
	}

	public void putAll(Map<? extends String, ? extends Object> out) {
		for (Entry<? extends String, ? extends Object> entry : out.entrySet())
			map.put(entry.getKey().toLowerCase(), entry.getValue());
	}

	public Object remove(Object key) {
		return map.remove(key.toString().toLowerCase());
	}

	public int size() {
		return map.size();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public Chain toChain() {
		return Chain.from(map);
	}

}
