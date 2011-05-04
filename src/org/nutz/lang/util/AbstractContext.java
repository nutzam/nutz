package org.nutz.lang.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;

public abstract class AbstractContext implements Context {

	public AbstractContext() {
		super();
	}

	public <T> T getAs(Class<T> type, String name) {
		return Castors.me().castTo(get(name), type);
	}

	public int getInt(String name) {
		return getAs(int.class, name);
	}

	public String getString(String name) {
		return getAs(String.class, name);
	}

	public boolean getBoolean(String name) {
		return getAs(boolean.class, name);
	}

	public float getFloat(String name) {
		return getAs(float.class, name);
	}

	public Context putAll(Object obj) {
		if (null != obj) {
			// Context
			if (obj instanceof Context) {
				for (String key : ((Context) obj).keys())
					this.set(key, ((Context) obj).get(key));
			}
			// Map
			else if (obj instanceof Map<?, ?>) {
				for (Map.Entry<?, ?> en : ((Map<?, ?>) obj).entrySet())
					this.set(en.getKey().toString(), en.getValue());
			}
			// 普通 Java 对象
			else {
				Mirror<?> mirror = Mirror.me(obj);
				// 需要被忽略的 Java 对象
				if (mirror.getType().isArray()
					|| mirror.isNumber()
					|| mirror.isBoolean()
					|| mirror.isChar()
					|| mirror.isStringLike()
					|| mirror.isDateTimeLike()
					|| Collection.class.isAssignableFrom(mirror.getType())) {}
				// 普通 Java 对象，应该取其每个字段
				else {
					for (Field field : mirror.getFields()) {
						this.set(field.getName(), mirror.getValue(obj, field));
					}
				}
			}
		}
		return this;
	}

	public Map<String, Object> getInnerMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : this.keys())
			map.put(key, this.get(key));
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap(String name) {
		return getAs(Map.class, name);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getList(String name) {
		return getAs(List.class, name);
	}

	public abstract AbstractContext clone();

}