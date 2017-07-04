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
import java.util.regex.Pattern;

import org.nutz.castor.Castors;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;

/**
 * 对于 LinkedHashMap 的一个友好封装
 * <p>
 * 同 TreeMap 不同的是，如果 get(null)，它不会抛错，就是返回 null 或默认值
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@SuppressWarnings("serial")
public class NutMap extends LinkedHashMap<String, Object> implements NutBean {

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

    public NutMap(String key, Object value) {
        super();
        put(key, value);
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

    public boolean is(String key, Object val) {
        Object obj = this.get(key);
        if (null == obj && null == val)
            return true;
        if (null == obj || null == val)
            return false;
        return obj.equals(val);
    }

    public NutMap duplicate() {
        NutMap map = new NutMap();
        map.putAll(this);
        return map;
    }

    /**
     * 从 Map 里挑选一些键生成一个新的 Map
     * 
     * @param keys
     *            键
     * @return 新 Map
     */
    public NutMap pick(String... keys) {
        if (keys.length == 0)
            return new NutMap();
        NutMap re = new NutMap();
        for (Map.Entry<String, Object> en : this.entrySet()) {
            String key = en.getKey();
            if (Lang.contains(keys, key)) {
                re.put(key, en.getValue());
            }
        }
        return re;
    }

    /**
     * 从 Map 里挑选一些键生成一个新的 Map，自己同时删除这些键
     * 
     * @param keys
     *            键
     * @return 新 Map
     */
    public NutMap pickAndRemove(String... keys) {
        if (keys.length == 0)
            return new NutMap();
        NutMap re = new NutMap();
        for (String key : keys) {
            Object val = this.remove(key);
            re.put(key, val);
        }
        return re;
    }

    /**
     * 从 Map 里挑选一些键生成一个新的 Map
     * 
     * @param regex
     *            匹配键的正则表达式，"!" 开头，表示取反
     * @return 新 Map
     */
    public NutMap pickBy(String regex) {
        if (Strings.isBlank(regex))
            return this.duplicate();
        boolean isNot = regex.startsWith("!");
        Pattern p = Pattern.compile(isNot ? regex.substring(1) : regex);
        return pickBy(p, isNot);
    }

    /**
     * 从 Map 里挑选一些键生成一个新的 Map
     * 
     * @param p
     *            匹配键的正则表达式，null 不会匹配任何一个键
     * @param isNot
     *            true 表示被匹配上的会被忽略，false 表示被匹配上的才加入到返回的集合里
     * @return 新 Map
     */
    public NutMap pickBy(Pattern p, boolean isNot) {
        // 一定不匹配
        if (null == p) {
            return isNot ? this.duplicate() : new NutMap();
        }

        // 挑选
        NutMap re = new NutMap();
        for (Map.Entry<String, Object> en : this.entrySet()) {
            String key = en.getKey();
            boolean matched = p.matcher(key).find();
            if (matched) {
                if (!isNot) {
                    re.put(key, en.getValue());
                }
            } else if (isNot) {
                re.put(key, en.getValue());
            }
        }

        // 返回
        return re;
    }

    /**
     * 就是 pickAndRemoveBy 的一个便利写法
     * 
     * @param regex
     *            正则表达式，! 开头表示取反
     * @return 新 Map
     * 
     * @see #pickAndRemoveBy(Pattern, boolean)
     */
    public NutMap pickAndRemoveBy(String regex) {
        if (Strings.isBlank(regex))
            return new NutMap();
        boolean isNot = regex.startsWith("!");
        Pattern p = Pattern.compile(isNot ? regex.substring(1) : regex);
        return pickAndRemoveBy(p, isNot);
    }

    /**
     * 从 Map 里挑选一些键生成一个新的 Map，自己同时删除这些键
     * 
     * @param p
     *            匹配键的正则表达式，null 不会匹配任何一个键
     * @param isNot
     *            true 表示被匹配上的会被忽略，false 表示被匹配上的才加入到返回的集合里
     * @return 新 Map
     */
    public NutMap pickAndRemoveBy(Pattern p, boolean isNot) {
        // 一定不匹配
        if (null == p) {
            if (isNot) {
                NutMap re = this.duplicate();
                this.clear();
                return re;
            } else {
                return new NutMap();
            }
        }

        // 挑选
        NutMap re = new NutMap();
        List<String> delKeys = new ArrayList<String>(this.size());
        for (Map.Entry<String, Object> en : this.entrySet()) {
            String key = en.getKey();
            boolean matched = p.matcher(key).find();
            if (matched) {
                if (!isNot) {
                    delKeys.add(key);
                    re.put(key, en.getValue());
                }
            } else if (isNot) {
                delKeys.add(key);
                re.put(key, en.getValue());
            }
        }

        // 删除 Key
        for (String key : delKeys)
            this.remove(key);

        // 返回
        return re;
    }

    /**
     * 从 Map 里将指定的键过滤，生成一个新的 Map
     * 
     * @param keys
     *            键
     * @return 新 Map
     */
    public NutMap omit(String... keys) {
        NutMap re = new NutMap();
        for (Map.Entry<String, Object> en : this.entrySet()) {
            String key = en.getKey();
            if (!Lang.contains(keys, key)) {
                re.put(key, en.getValue());
            }
        }
        return re;
    }

    /**
     * 如果一个键的值无效（has(key) 返回 false)，那么为其设置默认值
     * 
     * @param key
     *            键
     * @param dft
     *            值
     * @return 自身以便链式赋值
     */
    public NutMap putDefault(String key, Object dft) {
        if (!this.has(key)) {
            this.put(key, dft);
        }
        return this;
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
        for (String key : this.keySet()) {
            vals.add(this.get(key));
        }
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
        if (v instanceof CharSequence)
            return v.toString();
        if (v instanceof List) {
            v = ((List) v).iterator().next();
        }
        // by wendal : 这还有必要castTo么?
        // zozoh: 当然有啦，比如日期对象，要变成字符串的话 ...
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

        int len = Lang.eleSize(v);
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

        int len = Lang.eleSize(v);
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
     * 向某个键增加一组值，如果原来就有值，是集合的话，会被合并，否则原来的值用列表包裹后再加入新值
     * 
     * @param key
     *            键
     * @param values
     *            值列表
     * @return 自身
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> NutMap pushTo(String key, T... values) {
        if (null != values && values.length > 0) {
            Object v = get(key);
            // 不存在的话，增加列表
            if (null == v) {
                List<Object> list = new LinkedList<Object>();
                for (Object val : values)
                    list.add(val);
                this.put(key, list);
            }
            // 如果是集合的话，就增加
            else if (v instanceof Collection) {
                for (Object val : values)
                    ((Collection) v).add(val);
            }
            // 否则将原来的值变成列表再增加
            else {
                List<Object> list = new LinkedList<Object>();
                list.add(v);
                for (Object val : values)
                    list.add(val);
                this.put(key, list);
            }
        }
        // 返回自身以便链式赋值
        return this;
    }

    /**
     * 是 pushTo 函数的另一个变种（可以接受集合）
     * 
     * @param key
     *            键
     * @param values
     *            值列表
     * @return 自身
     * 
     * @see #pushTo(String, Collection)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public NutMap pushTo(String key, Collection<?> values) {
        if (null != values && values.size() > 0) {
            Object v = get(key);
            // 不存在的话，增加列表
            if (null == v) {
                List<Object> list = new LinkedList<Object>();
                list.addAll(values);
                this.put(key, list);
            }
            // 如果是集合的话，就增加
            else if (v instanceof Collection) {
                ((Collection) v).addAll(values);
            }
            // 否则将原来的值变成列表再增加
            else {
                List<Object> list = new LinkedList<Object>();
                list.add(v);
                list.addAll(values);
                this.put(key, list);
            }
        }
        // 返回自身以便链式赋值
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

    /**
     * 相当于 mergeWith(map, false)
     * 
     * @see #mergeWith(Map, boolean)
     */
    public NutMap mergeWith(Map<String, Object> map) {
        return this.mergeWith(map, false);
    }

    /**
     * 与一个给定的 Map 融合，如果有子 Map 递归
     * 
     * @param map
     *            要合并进来的 Map
     * @param onlyAbsent
     *            true 表示只有没有 key 才设置值
     * @return 自身以便链式赋值
     */
    @SuppressWarnings("unchecked")
    public NutMap mergeWith(Map<String, Object> map, boolean onlyAbsent) {
        for (Map.Entry<String, Object> en : map.entrySet()) {
            String key = en.getKey();
            Object val = en.getValue();

            if (null == key || null == val)
                continue;

            Object myVal = this.get(key);

            // 如果两边都是 Map ，则融合
            if (null != myVal && myVal instanceof Map && val instanceof Map) {
                Map<String, Object> m0 = (Map<String, Object>) myVal;
                Map<String, Object> m1 = (Map<String, Object>) val;
                NutMap m2 = NutMap.WRAP(m0).mergeWith(m1, onlyAbsent);
                // 搞出了新 Map，设置一下
                if (m2 != m0)
                    this.put(key, m2);
            }
            // 只有没有的时候才设置
            else if (onlyAbsent) {
                this.setnx(key, val);
            }
            // 否则直接替换
            else {
                this.put(key, val);
            }
        }

        return this;
    }

    /**
     * 与JDK8+的 putIfAbsent(key, val)一致, 当且仅当值不存在时设置进去,但与putIfAbsent返回值有不一样
     * 
     * @param key
     *            键
     * @param val
     *            值
     * @return 当前的NutMap实例
     */
    public NutMap setnx(String key, Object val) {
        if (!containsKey(key))
            setv(key, val);
        return this;
    }

    /**
     * 将一个集合与自己补充（相当于针对每个 key 调用 setnx)
     * 
     * @param map
     *            集合
     * @return 自身
     * 
     * @see #setnx(String, Object)
     */
    public NutMap setnxAll(Map<String, Object> map) {
        if (null != map && map.size() > 0) {
            for (Map.Entry<String, Object> en : map.entrySet()) {
                this.setnx(en.getKey(), en.getValue());
            }
        }
        return this;
    }

    /**
     * 获取对应的值,若不存在,用factory创建一个,然后设置进去,返回之
     * 
     * @param key
     *            键
     * @param factory
     *            若不存在的话用于生成实例
     * @return 已存在的值或新的值
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrBorn(String key, Borning<T> factory) {
        T t = (T) get(key);
        if (t == null) {
            t = factory.born(key);
            put(key, t);
        }
        return t;
    }

    /**
     * 将自身作为一个条件，看看给定的 Map 是否全部满足这个条件
     * <p>
     * 注意，字符串型的值有下列意义
     * <ul>
     * <li>"^xxxxx" : 正则表达式
     * <li>"" : 相当于 Blank
     * </ul>
     * 
     * @param map
     *            给定的 Map
     * @return 是否匹配
     */
    public boolean match(Map<String, Object> map) {
        // 空 map 一定是不匹配的
        if (null == map)
            return false;

        // 本 Map 如果没值，表示全匹配
        if (this.size() == 0)
            return true;

        // 逐个匹配键
        for (Map.Entry<String, Object> en : this.entrySet()) {
            String key = en.getKey();
            Object mtc = en.getValue();

            // null 表示对方不能包括这个键
            if (null == mtc) {
                if (map.containsKey(key))
                    return false;
            }
            // 其他的值，匹配一下
            else {
                Object val = map.get(key);
                if (!__match_val(mtc, val)) {
                    return false;
                }
            }
        }
        // 都检查过了 ...
        return true;
    }

    private boolean __match_val(final Object mtc, Object val) {
        Mirror<?> mi = Mirror.me(mtc);

        // 如果为 null，则只有空串能匹配
        if (null == val) {
            return mi.isStringLike() && Strings.isEmpty(mtc.toString());
        }

        // 字符串的话
        Pattern regex = mi.is(Pattern.class) ? (Pattern) mtc : null;
        if (mi.isStringLike()) {

            final String s = mtc.toString();
            if (s.startsWith("^")) {
                regex = Pattern.compile(s);
            }
            // 不是正则表达式，那么精确匹配字符串
            else {
                final boolean[] re = new boolean[1];
                Lang.each(val, new Each<Object>() {
                    public void invoke(int index, Object ele, int length) {
                        if (null != ele && ele.equals(s)) {
                            re[0] = true;
                            Lang.Break();
                        }
                    }
                });
                return re[0];
            }
        }

        // 正则表达式
        if (null != regex) {
            final boolean[] re = new boolean[1];
            final Pattern REG = regex;
            Lang.each(val, new Each<Object>() {
                public void invoke(int index, Object ele, int length) {
                    if (null != ele && REG.matcher(ele.toString()).matches()) {
                        re[0] = true;
                        Lang.Break();
                    }
                }
            });
            return re[0];
        }

        // 简单类型的比较
        if (mi.isSimple()) {
            final boolean[] re = new boolean[1];
            Lang.each(val, new Each<Object>() {
                public void invoke(int index, Object ele, int length) {
                    if (null != ele && ele.equals(mtc)) {
                        re[0] = true;
                        Lang.Break();
                    }
                }
            });
            return re[0];
        }
        // 范围的话...
        else if (mi.is(Region.class)) {
            throw Lang.noImplement();
        }
        // 其他的统统为不匹配
        return false;
    }
}
