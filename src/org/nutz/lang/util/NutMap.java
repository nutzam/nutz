package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

/**
 * 对于 TreeMap 的一个友好封装
 * <p>
 * 同 TreeMap 不同的是，如果 get(null)，它不会抛错，就是返回 null 或默认值
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@SuppressWarnings("serial")
public class NutMap extends TreeMap<String, Object> {

    public NutMap() {
        super();
    }

    public NutMap(Map<String, Object> map) {
        super();
        this.putAll(map);
    }

    public NutMap(String json) {
        super();
        this.putAll(Lang.map(json));
    }

    public int getInt(String key) {
        return getAs(int.class, key, -1);
    }

    public int getInt(String key, int dft) {
        return getAs(int.class, key, dft);
    }

    public String getString(String key) {
        return getAs(String.class, key, null);
    }

    public String getString(String key, String dft) {
        return getAs(String.class, key, dft);
    }

    public <T> List<T> getList(Class<T> eleType, String key) {
        Object obj = this.get(key);
        if (obj == null)
            return null;
        if (obj instanceof List<?>)
            return Lang.collection2list((List<?>) obj, eleType);
        if (obj.getClass().isArray())
            return Lang.array2list(obj, eleType);
        if (obj instanceof Map<?, ?>)
            return Lang.collection2list(((Map<?, ?>) obj).values(), eleType);
        List<T> list = new ArrayList<T>(1);
        list.add(Castors.me().castTo(obj, eleType));
        return list;
    }

    public <T> T getAs(Class<T> toType, String key) {
        return getAs(toType, key, null);
    }

    public <T> T getAs(Class<T> toType, String key, T dft) {
        if (null == key)
            return dft;
        Object obj = get(key);
        if (null == obj)
            return dft;
        return Castors.me().castTo(obj, toType);
    }

    /**
     * 为 Map 增加一个名值对。
     * <ul>
     * <li>如果该键不存在，则添加对象。
     * <li>如果存在并且是 List，则添加到 List。
     * <li>创建一个 List ，并添加对象
     * </ul>
     * 
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public NutMap add(String key, Object value) {
        Object obj = get(key);
        if (null == obj)
            put(key, value);
        else if (obj instanceof List<?>)
            ((List<Object>) obj).add(value);
        else {
            List<Object> list = new LinkedList<Object>();
            list.add(obj);
            list.add(value);
            put(key, list);
        }
        return this;
    }
}
