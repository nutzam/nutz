package org.nutz.lang.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Context extends Cloneable {

    Context set(String name, Object value);

    Set<String> keys();

    Map<String, Object> getInnerMap();

    Context putAll(Object obj);

    Context putAll(String prefix, Object obj);

    boolean has(String key);

    Context clear();

    int size();

    boolean isEmpty();

    Object get(String name);

    Object get(String name, Object dft);

    <T> T getAs(Class<T> type, String name);

    <T> T getAs(Class<T> type, String name, T dft);

    int getInt(String name);

    int getInt(String name, int dft);

    String getString(String name);

    String getString(String name, String dft);

    boolean getBoolean(String name);

    boolean getBoolean(String name, boolean dft);

    float getFloat(String name);

    float getFloat(String name, float dft);

    double getDouble(String name);

    double getDouble(String name, double dft);

    Map<String, Object> getMap(String name);

    List<Object> getList(String name);

    <T> List<T> getList(Class<T> classOfT, String name);

    Context clone();

}