package org.nutz.lang.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface NutBean extends Map<String, Object> {

    boolean has(String key);

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

    /**
     * 将一个字段转换成列表。因为返回的是容器，所以本函数永远不会返回 null
     * 
     * @param <T>
     * @param key
     * @param eleType
     * @return 列表对象，如果字段不存在或者为空，则返回一个空列表
     */
    <T> List<T> getList(String key, Class<T> eleType);

    /**
     * 将一个字段转换成数组。因为返回的是容器，所以本函数永远不会返回 null
     * 
     * @param <T>
     * @param key
     * @param eleType
     * @return 数组对象，如果字段不存在或者为空，则返回一个空数组
     */
    <T> T[] getArray(String key, Class<T> eleType);

    NutBean setv(String key, Object value);

    void unset(String key);

    NutBean setAll(Map<String, Object> map);
}