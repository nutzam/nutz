package org.nutz.dao.entity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.castor.Castors;
import org.nutz.dao.Chain;
import org.nutz.dao.DaoException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

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
    
    private static final Log log = Logs.get();
    
    protected static int DEFAULT_INT = -1;

    /** change as you own risk!!
     * <p/> 除非你很清楚自己在干啥,否则不要碰这个值!!
     * */
    public static void __you_own_risk_changeDefaultIntNumber(int i) {
    	DEFAULT_INT = i;
    	log.info("!!!!!!!!!!!! NOW !! org.nutz.dao.entity.Record.DEFAULT_INT = " + i);
    	log.info("!!!!!!!!!!!! NOW !! org.nutz.dao.entity.Record.DEFAULT_INT = " + i);
    	log.warn("!!!!!!!!!!!! NOW !! org.nutz.dao.entity.Record.DEFAULT_INT = " + i);
    	log.warn("!!!!!!!!!!!! NOW !! org.nutz.dao.entity.Record.DEFAULT_INT = " + i);
    	log.error("!!!!!!!!!!!! NOW !! org.nutz.dao.entity.Record.DEFAULT_INT = " + i);
    	log.error("!!!!!!!!!!!! NOW !! org.nutz.dao.entity.Record.DEFAULT_INT = " + i);
    }

    /**
     * 根据 ResultSet 创建一个记录对象
     * 
     * @param rs
     *            ResultSet 对象
     * @return 记录对象
     */
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
        map = new LinkedHashMap<String, Object>();
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
     * 返回记录中已有的字段的数量
     * 
     * @return 记录中已有的字段的数量
     */
    public int getColumnCount() {
        return map.size();
    }

    /**
     * 返回记录中所有的字段名
     * 
     * @return 记录中所有的字段名
     */
    public Set<String> getColumnNames() {
        return map.keySet();
    }

    /**
     * 返回指定字段的 int 值
     * <p>
     * 如果该字段在记录中不存在，返回 -1；如果该字段的值不是 int 类型，返回 -1
     * 
     * @param name
     *            字段名
     * @return 指定字段名的 int 值。如果该字段在记录中不存在，返回 -1；如果该字段的值不是 int 类型，返回 -1
     */
    public int getInt(String name) {
        try {
            Object val = get(name);
            if (null == val)
                return DEFAULT_INT;
            return Castors.me().castTo(val, int.class);
        }
        catch (Exception e) {}
        return DEFAULT_INT;
    }

    /**
     * 返回指定字段的 String 值
     * <p>
     * 如果该字段在记录中不存在，返回 null
     * 
     * @param name
     *            字段名
     * @return 指定字段的 String 值。如果该字段在记录中不存在，返回 null
     */
    public String getString(String name) {
        Object val = get(name);
        if (null == val)
            return null;
        return Castors.me().castToString(val);
    }

    /**
     * 返回指定字段的 Timestamp 值
     * <p>
     * 如果该字段在记录中不存在，返回 null
     * 
     * @param name
     *            字段名
     * @return 指定字段的 Timestamp 值。如果该字段在记录中不存在，返回 null
     */
    public Timestamp getTimestamp(String name) {
        Object val = get(name);
        if (null == val)
            return null;
        return Castors.me().castTo(val, Timestamp.class);
    }

    /**
     * 返回该记录的 JSON 字符串，并且可以设定 JSON 字符串的格式化方式
     * 
     * @param format
     *            JSON 字符串格式化方式 ，若 format 为 null ，则以 JsonFormat.nice() 格式输出
     * @return JSON 字符串
     */
    public String toJson(JsonFormat format) {
        return Json.toJson(map, format);
    }

    /**
     * 返回该记录 JSON 格式的字符串表示
     * 
     * @return 该记录 JSON 格式的字符串表示
     */
    public String toString() {
        return Json.toJson(map);
    }

    /**
     * 根据指定的类的类型，把该记录转换成该类型的对象
     * 
     * @param type
     *            指定的类的类型
     * @return 指定的类型的对象
     */
    public <T> T toPojo(Class<T> type) {
        return Lang.map2Object(map, type);
    }

    public <T> T toEntity(Entity<T> en) {
        return en.getObject(this);
    }

    /**
     * 从记录中移除所有字段与值的对应关系
     */
    public void clear() {
        map.clear();
    }

    /**
     * 如果该字段在记录中存在，则返回 true
     * 
     * @param key
     *            字段名
     * @return true 该字段在记录中存在
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key.toString().toLowerCase());
    }

    /**
     * 如果该字段值在记录中存在，则返回 true
     * 
     * @param value
     *            字段值
     * @return true 该字段值在记录中存在
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object out) {
        return map.equals(out);
    }

    /**
     * 返回指定字段的值
     * <p>
     * 如果该字段在记录中不存在，返回 null
     * 
     * @param name
     *            字段名
     * @return 指定字段的值。如果该字段在记录中不存在，返回 null
     */
    public Object get(Object name) {
        if (null == name)
            return null;
        return map.get(name.toString().toLowerCase());
    }

    /**
     * 返回该记录的哈希码值
     */
    public int hashCode() {
        return map.hashCode();
    }

    /**
     * 如果记录中不存在字段与值的对应关系，则返回 true
     * 
     * @return true 记录中不存在字段与值的对应关系
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * 返回记录中所有的字段名
     * 
     * @return 记录中所有的字段名
     */
    public Set<String> keySet() {
        return map.keySet();
    }

    /**
     * 将字段与其对应的值放入该记录中
     * 
     * @param name
     *            字段名
     * @param value
     *            字段值
     * @return 该字段之前所对应的值；如果之前该字段在该记录中不存在，则返回 null
     */
    public Object put(String name, Object value) {
        return map.put(name.toLowerCase(), value);
    }

    public void putAll(Map<? extends String, ? extends Object> out) {
        for (Entry<? extends String, ? extends Object> entry : out.entrySet())
            map.put(entry.getKey().toLowerCase(), entry.getValue());
    }

    /**
     * 将字段从记录中删除
     * 
     * @param key
     *            字段名
     * @return 该字段所对应的值；如果该字段在该记录中不存在，则返回 null
     */
    public Object remove(Object key) {
        return map.remove(key.toString().toLowerCase());
    }

    /**
     * 返回记录的记录数
     * 
     * @return 记录的记录数
     */
    public int size() {
        return map.size();
    }

    /**
     * 返回记录中所有的字段的值
     * 
     * @return 记录中所有的字段的值
     */
    public Collection<Object> values() {
        return map.values();
    }

    /**
     * 返回该记录对应的 Chain 对象
     * 
     * @return 该记录对应的 Chain 对象
     */
    public Chain toChain() {
        return Chain.from(map);
    }

    // ===========================================

    /**
     * 返回该字段对应的数据库类型
     * 
     * @param name
     *            字段名
     * @return 该字段对应的数据库类型
     */
    public int getSqlType(String name) {
        return sqlTypeMap.get(name.toLowerCase());
    }

    /**
     * 设置该字段对应的数据库类型
     * 
     * @param name
     *            字段名
     * @param value
     *            数据库类型
     */
    protected void setSqlType(String name, int value) {
        sqlTypeMap.put(name.toLowerCase(), value);
    }
}
