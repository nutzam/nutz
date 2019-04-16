package org.nutz.dao.entity;

import org.nutz.castor.Castors;
import org.nutz.dao.Chain;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.jdbc.BlobValueAdaptor;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * 记录对象
 *
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author wizzercn(wizzer.cn@gmail.com)
 */
public class Record implements Map<String, Object>, java.io.Serializable, Cloneable, Comparable<Record> {

    private static final long serialVersionUID = -7753504263747912181L;

    protected static Callable<Record> factory;

    /**
     * 根据 ResultSet 创建一个记录对象
     *
     * @param rs ResultSet 对象
     * @return 记录对象
     */
    public static Record create(ResultSet rs) {
        Record re = create();
        create(re, rs, null);
        return re;
    }

    public static void create(Map<String, Object> re, ResultSet rs, ResultSetMetaData meta) {
        String name = null;
        int i = 0;
        try {
            if (meta == null)
                meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (i = 1; i <= count; i++) {
                name = meta.getColumnLabel(i);
                switch (meta.getColumnType(i)) {
                    case Types.TIMESTAMP: {
                        re.put(name, rs.getTimestamp(i));
                        break;
                    }
                    case Types.DATE: {// ORACLE的DATE类型包含时间,如果用默认的只有日期没有时间 from
                        // cqyunqin
                        re.put(name, rs.getTimestamp(i));
                        break;
                    }
                    case Types.CLOB: {
                        re.put(name, rs.getString(i));
                        break;
                    }
                    case Types.BLOB: {
                        re.put(name, new BlobValueAdaptor(Jdbcs.getFilePool()).get(rs, name));
                        break;
                    }
                    default:
                        re.put(name, rs.getObject(i));
                        break;
                }
            }
        } catch (SQLException e) {
            if (name != null) {
                throw new DaoException(String.format("Column Name=%s, index=%d", name, i), e);
            }
            throw new DaoException(e);
        }
    }

    private Map<String, Object> map;
    private List<String> keys;

    public Record() {
        map = new LinkedHashMap<String, Object>();
        keys = new ArrayList<String>();
    }

    /**
     * 设置值
     *
     * @param name  字段名
     * @param value 字段值
     * @return 记录本身
     */
    public Record set(String name, Object value) {
        map.put(name.toLowerCase(), value);
        keys.add(name);
        return this;
    }

    /**
     * 移除一个字段
     *
     * @param name 字段名
     * @return 移除的字段值
     */
    public Object remove(String name) {
        keys.remove(name);
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
     * @param name 字段名
     * @return 指定字段名的 int 值。如果该字段在记录中不存在，返回 -1；如果该字段的值不是 int 类型，返回 -1
     */
    public int getInt(String name) {
        return getInt(name, -1);
    }

    public int getInt(String name, int dft) {
        try {
            Object val = get(name);
            if (null == val)
                return dft;
            return Castors.me().castTo(val, int.class);
        } catch (Exception e) {
        }
        return dft;
    }

    public long getLong(String name) {
        return getLong(name, -1);
    }

    public long getLong(String name, long dft) {
        try {
            Object val = get(name);
            if (null == val)
                return dft;
            return Castors.me().castTo(val, long.class);
        } catch (Exception e) {
        }
        return dft;
    }

    public double getDouble(String name) {
        return getDouble(name, -1);
    }

    public double getDouble(String name, double dft) {
        try {
            Object val = get(name);
            if (null == val)
                return dft;
            return Castors.me().castTo(val, double.class);
        } catch (Exception e) {
        }
        return dft;
    }

    /**
     * 返回指定字段的 String 值
     * <p>
     * 如果该字段在记录中不存在，返回 null
     *
     * @param name 字段名
     * @return 指定字段的 String 值。如果该字段在记录中不存在，返回 null
     */
    public String getString(String name) {
        Object val = get(name);
        if (null == val)
            return null;
        return Castors.me().castToString(val);
    }

    /**
     * 返回指定字段的 Blob 值
     * <p>
     * 如果该字段在记录中不存在，返回 null
     *
     * @param name 字段名
     * @return 指定字段的 Blob 值。如果该字段在记录中不存在，返回 null
     */
    public Blob getBlob(String name) {
        Object val = get(name);
        if (null == val)
            return null;
        return Castors.me().castTo(val, Blob.class);
    }

    /**
     * 返回指定字段的 Timestamp 值
     * <p>
     * 如果该字段在记录中不存在，返回 null
     *
     * @param name 字段名
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
     * @param format JSON 字符串格式化方式 ，若 format 为 null ，则以 JsonFormat.nice() 格式输出
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
        return Json.toJson(map, JsonFormat.full());
    }

    /**
     * 根据指定的类的类型，把该记录转换成该类型的对象
     *
     * @param type 指定的类的类型
     * @return 指定的类型的对象
     */
    public <T> T toPojo(Class<T> type) {
        return Lang.map2Object(this, type);
    }

    public <T> T toEntity(Entity<T> en) {
        return en.getObject(this);
    }

    public <T> T toEntity(Entity<T> en, String prefix) {
        return en.getObject(this, prefix);
    }

    /**
     * 从记录中移除所有字段与值的对应关系
     */
    public void clear() {
        map.clear();
        keys.clear();
    }

    /**
     * 如果该字段在记录中存在，则返回 true
     *
     * @param key 字段名
     * @return true 该字段在记录中存在
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key.toString().toLowerCase());
    }

    /**
     * 如果该字段值在记录中存在，则返回 true
     *
     * @param value 字段值
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
     * @param name 字段名
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
     * @param name  字段名
     * @param value 字段值
     * @return 该字段之前所对应的值；如果之前该字段在该记录中不存在，则返回 null
     */
    public Object put(String name, Object value) {
        keys.add(name);
        return map.put(name.toLowerCase(), value);
    }

    public void putAll(Map<? extends String, ? extends Object> out) {
        for (Entry<? extends String, ? extends Object> entry : out.entrySet())
            put(entry.getKey(), entry.getValue());
    }

    /**
     * 将字段从记录中删除
     *
     * @param key 字段名
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

    public Record clone() {
        Record re = create();
        re.putAll(this);
        return re;
    }

    public Map<String, Object> sensitive() {
        NutMap map = new NutMap();
        for (String key : keys) {
            map.put(key, get(key));
        }
        return map;
    }

    public int compareTo(Record re) {
        if (re == null)
            return 1;
        if (re.size() == this.size())
            return 0;
        return re.size() > this.size() ? -1 : 1;
    }

    public static void setFactory(Callable<Record> factory) {
        Record.factory = factory;
    }

    public static Record create() {
        if (factory != null)
            try {
                return factory.call();
            } catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
        return new Record();
    }
}
