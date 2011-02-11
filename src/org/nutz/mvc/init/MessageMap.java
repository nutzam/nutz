package org.nutz.mvc.init;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;

public class MessageMap implements Map<String, String> {

	private Map<String, String> map;

	public MessageMap() {
		map = new HashMap<String, String>();
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return map.entrySet();
	}

	/**
	 * 当获取一个字符串时，如果没有该字符串，则返回该字符串键值
	 */
	public String get(Object key) {
		String re = map.get(key);
		if (Strings.isBlank(re))
			return key.toString();
		return re;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public String put(String key, String value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		map.putAll(m);
	}

	public String remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<String> values() {
		return map.values();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public int hashCode() {
		return map.hashCode();
	}

	public String toString() {
		return Json.toJson(map, JsonFormat.nice());
	}

	public String toJson() {
		return Json.toJson(map);
	}

}
