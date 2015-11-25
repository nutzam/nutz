package org.nutz.lang.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.castor.Castors;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 对于 LinkedHashMap 的一个友好封装
 * <p>
 * 同 TreeMap 不同的是，如果 get(null)，它不会抛错，就是返回 null 或默认值
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@SuppressWarnings("serial")
public class NutMap extends LinkedHashMap<String, Object>implements NutBean {

    public static NutMap WRAP(Map<String, Object> map) {
        if (null == map)
            return null;
        if (map instanceof NutMap)
            return (NutMap) map;
        return new NutMap(map);
    }

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

    /**
     * 设置一个字段，如果值为 null 则表示移除
     * 
     * @param key
     *            键
     * @param v
     *            值
     */
    public void setOrRemove(String key, Object v) {
        if (null == v) {
            this.remove(key);
        } else {
            this.put(key, v);
        }
    }

    public static NutMap NEW() {
        return new NutMap();
    }

    public static NutMap WRAP(String json) {
        return new NutMap(json);
    }

    public boolean has(String key) {
        return null != get(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (null == _map)
            return super.containsValue(value);
        return super.containsValue(value) || _map.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        if (null == _map)
            return super.containsKey(key);
        return super.containsKey(key) || _map.containsKey(key);
    }

    public Set<String> keySet() {
        if (null == _map)
            return super.keySet();
        HashSet<String> keys = new HashSet<String>();
        keys.addAll(super.keySet());
        keys.addAll(_map.keySet());
        return keys;
    }

    public Collection<Object> values() {
        if (null == _map)
            return super.values();
        List<Object> vals = new LinkedList<Object>();
        vals.addAll(super.values());
        vals.addAll(_map.values());
        return vals;
    }

    public Set<Entry<String, Object>> entrySet() {
        if (null == _map)
            return super.entrySet();
        HashSet<Entry<String, Object>> vals = new HashSet<Entry<String, Object>>();
        vals.addAll(_map.entrySet());
        vals.addAll(super.entrySet());
        return vals;
    }

    public void clear() {
        super.clear();
        if (null != _map)
            _map.clear();
    }

    private NutMap _map;

    public NutMap attach(NutMap map) {
        _map = map;
        return this;
    }

    public NutMap detach() {
        NutMap re = _map;
        _map = null;
        return re;
    }

    @Override
    public Object get(Object key) {
        if (_map == null)
            return super.get(key);

        if (super.containsKey(key)) {
            return super.get(key);
        }

        return _map.get(key);
    }

    public Object get(String key, Object dft) {
        Object v = get(key);
        return null == v ? dft : v;
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, int.class);
    }

    public float getFloat(String key) {
        return getFloat(key, Float.NaN);
    }

    public float getFloat(String key, float dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, float.class);
    }

    public long getLong(String key) {
        return getLong(key, -1);
    }

    public long getLong(String key, long dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, long.class);
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public double getDouble(String key, double dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, double.class);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, boolean.class);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    @SuppressWarnings("rawtypes")
    public String getString(String key, String dft) {
        Object v = get(key);
        if (v == null)
            return dft;
        if (v instanceof List) {
            v = ((List) v).iterator().next();
        }
        // by wendal : 这还有必要castTo么?
        return Castors.me().castTo(v, String.class);
    }

    public Date getTime(String key) {
        return getTime(key, null);
    }

    public Date getTime(String key, Date dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, Date.class);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> classOfEnum) {
        String s = getString(key);
        if (Strings.isBlank(s))
            return null;
        return Enum.valueOf(classOfEnum, s);
    }

    @SuppressWarnings("unchecked")
    public boolean isEnum(String key, Enum<?>... eus) {
        if (null == eus || eus.length == 0)
            return false;
        try {
            Enum<?> v = getEnum(key, eus[0].getClass());
            for (Enum<?> eu : eus)
                if (!v.equals(eu))
                    return false;
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public <T> T getAs(String key, Class<T> classOfT) {
        return getAs(key, classOfT, null);
    }

    public <T> T getAs(String key, Class<T> classOfT, T dft) {
        Object v = get(key);
        return null == v ? dft : Castors.me().castTo(v, classOfT);
    }

    public <T> List<T> getList(String key, final Class<T> eleType) {
        return getList(key, eleType, new ArrayList<T>());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, final Class<T> eleType, List<T> dft) {
        Object v = get(key);
        if (null == v)
            return dft;

        if (v instanceof CharSequence) {
            return Lang.list(Castors.me().castTo(v, eleType));
        }

        int len = Lang.length(v);
        final List<T> list = new ArrayList<T>(len);
        Lang.each(v, new Each<Object>() {
            public void invoke(int index, Object ele, int length) {
                list.add(Castors.me().castTo(ele, eleType));
            }
        });

        return list;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] getArray(String key, Class<T> eleType) {
        return getArray(key, eleType, (T[]) Array.newInstance(eleType, 0));
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(String key, final Class<T> eleType, T[] dft) {
        Object v = get(key);
        if (null == v)
            return dft;

        if (v instanceof CharSequence) {
            return Lang.array(Castors.me().castTo(v, eleType));
        }

        int len = Lang.length(v);
        final Object arr = Array.newInstance(eleType, len);
        final int[] i = new int[]{0};
        Lang.each(v, new Each<Object>() {
            public void invoke(int index, Object ele, int length) {
                Array.set(arr, i[0]++, Castors.me().castTo(ele, eleType));
            }
        });

        return (T[]) arr;

    }

    /**
     * 为 Map 增加一个名值对。如果同名已经有值了，那么会将两个值合并成一个列表
     * 
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public NutMap addv(String key, Object value) {
        Object obj = get(key);
        if (null == obj) {
            put(key, value);
        } else if (obj instanceof List<?>)
            ((List<Object>) obj).add(value);
        else {
            List<Object> list = new LinkedList<Object>();
            list.add(obj);
            list.add(value);
            put(key, list);
        }
        return this;
    }

    /**
     * @deprecated 本函数意义容易发生混淆，已经改名成 addv，下个版将被删除
     * @since 1.b.51
     */
    public NutMap putv(String key, Object value) {
        return addv(key, value);
    }

    public NutMap setv(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public void unset(String key) {
        this.remove(key);
    }

    public NutBean setAll(Map<String, Object> map) {
        this.putAll(map);
        return this;
    }

    public NutMap setMap(Map<?, ?> map, boolean ignoreNullValue) {
        for (Map.Entry<?, ?> en : map.entrySet()) {
            Object key = en.getKey();
            Object val = en.getValue();

            if (null == key)
                continue;

            if (null == val && ignoreNullValue)
                continue;

            this.put(key.toString(), val);
        }
        return this;
    }
}
