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
import org.nutz.dao.DaoException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;

/**
 * 记录对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class Record implements Map<String, Object>, java.io.Serializable {

    /**
     * @author mawenming at Jan 11, 2011 2:20:09 PM
     */
    private static final long serialVersionUID = 4614645901639942051L;

    public static Record create(ResultSet rs) {
    	String name = null;
    	int i = 0;
        try {
            Record re = new Record();
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (i = 1; i <= count; i++) {
                name = meta.getColumnLabel(i);
                switch (meta.getColumnType(i)) {
                case Types.TIMESTAMP: {
                    re.set(name, rs.getTimestamp(i));
                    break;
                }
                case Types.DATE: {// ORACLE的DATE类型包含时间,如果用默认的只有日期没有时间 from
                                    // cqyunqin
                    re.set(name, rs.getTimestamp(i));
                    break;
                }
                case Types.CLOB: {
                    re.set(name, rs.getString(i));
                    break;
                }
                default:
                    re.set(name, rs.getObject(i));
                    break;
                }
                re.setSqlType(name, meta.getColumnType(i));
            }
            return re;
        }
        catch (SQLException e) {
        	if (name != null) {
        		throw new DaoException(String.format("Column Name=%s, index=%d", name, i), e);
        	}
            throw new DaoException(e);
        }
    }

    private Map<String, Object> map;

    private Map<String, Integer> sqlTypeMap;

    public Record() {
        map = new HashMap<String, Object>();
        sqlTypeMap = new HashMap<String, Integer>();
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
        try {
            Object val = get(name);
            if (null == val)
                return -1;
            return Castors.me().castTo(val, int.class);
        }
        catch (Exception e) {}
        return -1;
    }

    public String getString(String name) {
        Object val = get(name);
        if (null == val)
            return null;
        return Castors.me().castToString(val);
    }

    public Timestamp getTimestamp(String name) {
        Object val = get(name);
        if (null == val)
            return null;
        return Castors.me().castTo(val, Timestamp.class);
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

    public <T> T toEntity(Entity<T> en) {
        return en.getObject(this);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key.toString().toLowerCase());
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

    // ===========================================

    public int getSqlType(String name) {
        return sqlTypeMap.get(name.toLowerCase());
    }

    protected void setSqlType(String name, int value) {
        sqlTypeMap.put(name.toLowerCase(), value);
    }
}
