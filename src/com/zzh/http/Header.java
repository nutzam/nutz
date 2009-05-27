package com.zzh.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zzh.json.Json;
import com.zzh.json.JsonFormat;

public class Header {

	public Header() {
		items = new HashMap<String, String>();
	}

	public Header(Map<String, String> properties) {
		this();
		addAll(properties);
	}

	@SuppressWarnings("unchecked")
	public Header(String properties) {
		this((Map<String, String>) Json.fromJson(properties));
	}

	private Map<String, String> items;

	public Collection<String> keys() {
		return items.keySet();
	}

	public String get(String key) {
		return items.get(key);
	}

	public Header set(String key, String value) {
		if (null != key)
			items.put(key, value);
		return this;
	}

	public Header remove(String key) {
		items.remove(key);
		return this;
	}

	public Header clear() {
		items.clear();
		return this;
	}

	public Header addAll(Map<String, String> map) {
		if (null != map)
			for (String key : map.keySet())
				set(key, map.get(key));
		return this;
	}

	@Override
	public String toString() {
		return Json.toJson(items, JsonFormat.nice().setIgnoreNull(false));
	}

}
