package org.nutz.lang.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.lang.born.Borning;

public interface NutBean extends Map<String, Object> {

    boolean has(String key);

    boolean is(String key, Object val);

    /**
     * 设置一个字段，如果值为 null 则表示移除
     * 
     * @param key
     *            键
     * @param v
     *            值
     */
    void setOrRemove(String key, Object v);

    Object get(String key, Object dft);

    int getInt(String key);

    int getInt(String key, int dft);

    float getFloat(String key);

    float getFloat(String key, float dft);

    long getLong(String key);

    long getLong(String key, long dft);

    double getDouble(String key);

    double getDouble(String key, double dft);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean dft);

    String getString(String key);

    String getString(String key, String dft);

    Date getTime(String key);

    Date getTime(String key, Date dft);

    <T extends Enum<T>> T getEnum(String key, Class<T> classOfEnum);

    boolean isEnum(String key, Enum<?>... eus);

    <T> T getAs(String key, Class<T> classOfT);

    <T> T getAs(String key, Class<T> classOfT, T dft);

    <T> List<T> getAsList(String key, Class<T> eleType);

    /**
     * 将一个字段转换成列表。因为返回的是容器，所以本函数永远不会返回 null
     * 
     * @param <T>
     * @param key
     *            键
     * @param eleType
     *            列表元素类型
     * @param dft
     *            默认值
     * @return 列表对象，如果字段不存在或者为空，则返回一个空列表
     */
    <T> List<T> getList(String key, Class<T> eleType, List<T> dft);

    /**
     * @see #getList(String, Class, List)
     */
    <T> List<T> getList(String key, Class<T> eleType);

    /**
     * 将一个字段转换成数组。因为返回的是容器，所以本函数永远不会返回 null
     * 
     * @param <T>
     * @param key
     *            键
     * @param eleType
     *            数组元素类型
     * @param dft
     *            默认值
     * @return 数组对象，如果字段不存在或者为空，则返回一个空数组
     */
    <T> T[] getArray(String key, Class<T> eleType, T[] dft);

    /**
     * @see #getArray(String, Class, Object[])
     */
    <T> T[] getArray(String key, Class<T> eleType);

    NutBean addv(String key, Object value);

    NutBean setv(String key, Object value);

    void unset(String key);

    NutBean setAll(Map<String, Object> map);

    /**
     * 从 Map 里挑选一些键生成一个新的 Map
     * 
     * @param keys
     *            键
     * @return 新 Map
     */
    NutBean pick(String... keys);

    /**
     * 从 Map 里挑选一些键生成一个新的 Map，自己同时删除这些键
     * 
     * @param keys
     *            键
     * @return 新 Map
     */
    NutBean pickAndRemove(String... keys);

    /**
     * 从 Map 里挑选一些键生成一个新的 Map
     * 
     * @param regex
     *            匹配键的正则表达式，"!" 开头，表示取反
     * @return 新 Map
     */
    NutBean pickBy(String regex);

    /**
     * 从 Map 里挑选一些键生成一个新的 Map
     * 
     * @param p
     *            匹配键的正则表达式，null 不会匹配任何一个键
     * @param isNot
     *            true 表示被匹配上的会被忽略，false 表示被匹配上的才加入到返回的集合里
     * @return 新 Map
     */
    NutBean pickBy(Pattern p, boolean isNot);

    /**
     * 从 Map 里挑选一些键生成一个新的 Map，自己同时删除这些键
     * 
     * @param p
     *            匹配键的正则表达式，null 不会匹配任何一个键
     * @param isNot
     *            true 表示被匹配上的会被忽略，false 表示被匹配上的才加入到返回的集合里
     * @return 新 Map
     */
    NutBean pickAndRemoveBy(Pattern p, boolean isNot);

    /**
     * 从 Map 里将指定的键过滤，生成一个新的 Map
     * 
     * @param keys
     *            键
     * @return 新 Map
     */
    NutBean omit(String... keys);

    /**
     * 如果一个键的值无效（has(key) 返回 false)，那么为其设置默认值
     * 
     * @param key
     *            键
     * @param dft
     *            值
     * @return 自身以便链式赋值
     */
    NutBean putDefault(String key, Object dft);

    NutBean setMap(Map<?, ?> map, boolean ignoreNullValue);

    /**
     * 相当于 mergeWith(map, false)
     * 
     * @see #mergeWith(Map, boolean)
     */
    NutBean mergeWith(Map<String, Object> map);

    /**
     * 与一个给定的 Map 融合，如果有子 Map 递归
     * 
     * @param map
     *            要合并进来的 Map
     * @param onlyAbsent
     *            true 表示只有没有 key 才设置值
     * @return 自身以便链式赋值
     */
    NutBean mergeWith(Map<String, Object> map, boolean onlyAbsent);

    /**
     * 与JDK8+的 putIfAbsent(key, val)一致, 当且仅当值不存在时设置进去,但与putIfAbsent返回值有不一样
     * 
     * @param key
     *            键
     * @param val
     *            值
     * @return 当前实例
     */
    NutBean setnx(String key, Object val);

    /**
     * 将一个集合与自己补充（相当于针对每个 key 调用 setnx)
     * 
     * @param map
     *            集合
     * @return 自身
     * 
     * @see #setnx(String, Object)
     */
    NutBean setnxAll(Map<String, Object> map);

    /**
     * 获取对应的值,若不存在,用factory创建一个,然后设置进去,返回之
     * 
     * @param key
     *            键
     * @param factory
     *            若不存在的话用于生成实例
     * @return 已存在的值或新的值
     */
    <T> T getOrBorn(String key, Borning<T> factory);

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
    boolean match(Map<String, Object> map);
}