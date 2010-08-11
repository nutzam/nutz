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
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.ToJson;
import org.nutz.lang.Lang;

/**
 * 记录对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@ToJson
public class Record implements Map<String, Object> {

	public static Record create(ResultSet rs) {
		try {
			Record re = new Record();
			ResultSetMetaData meta = rs.getMetaData();
			int count = meta.getColumnCount();
			for (int i = 1; i <= count; i++) {
				String name = meta.getColumnLabel(i);
				if (meta.getColumnType(i) == Types.CLOB) {
					re.set(name.toLowerCase(), rs.getString(i));
				} else {
					re.set(name.toLowerCase(), rs.getObject(i));
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
		map.put(name, value);
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
		return map.remove(name);
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

	public <T> T toPojo(Class<T> type) {
		return Lang.map2Object(map, type);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object arg0) {
		return map.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		return map.containsValue(arg0);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object arg0) {
		return map.equals(arg0);
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

	public Object put(String arg0, Object arg1) {
		return map.put(arg0, arg1);
	}

	public void putAll(Map<? extends String, ? extends Object> arg0) {
		map.putAll(arg0);
	}

	public Object remove(Object arg0) {
		return map.remove(arg0);
	}

	public int size() {
		return map.size();
	}

	public Collection<Object> values() {
		return map.values();
	}

}
