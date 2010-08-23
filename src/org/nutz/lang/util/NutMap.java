package org.nutz.lang.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.nutz.castor.Castors;

@SuppressWarnings("serial")
public class NutMap extends HashMap<String, Object> {

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

	private <T> T getAs(Class<T> toType, String key, T dft) {
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
