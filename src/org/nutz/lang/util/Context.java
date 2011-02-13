package org.nutz.lang.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Context extends Cloneable {

	Context set(String name, Object value);

	Set<String> keys();

	Context putAll(Map<String, Object> map);

	Context putAll(Context context);

	boolean has(String key);

	Context clear();

	Object get(String name);

	<T> T getAs(Class<T> type, String name);

	int getInt(String name);

	String getString(String name);

	boolean getBoolean(String name);

	float getFloat(String name);

	Map<String, Object> getMap(String name);

	List<Object> getList(String name);

	Context clone();

}