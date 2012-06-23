package org.nutz.http.server;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.http.impl.Https;
import org.nutz.lang.Lang;

public class NutListMap {

	protected Map<String, List<String>> datas = new HashMap<String, List<String>>();
	public Map<String, List<String>> datas() {
		return datas;
	}
	
	public String get(String name) {
		List<String> hs = datas.get(name);
		if (hs == null || hs.isEmpty())
			return null;
		return hs.get(0);
	}
	
	public long getDate(String name) {
		String str = get(name);
		if (str == null)
			return -1;
		try {
			return Https.httpData(str).getTime();
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public int getInt(String name) {
		String str = get(name);
		if (str == null)
			return -1;
		return Integer.parseInt(str);
	}
	
	public Set<String> getNames() {
		return datas.keySet();
	}
	
	public List<String> gets(String name) {
		return datas.get(name);
	}
	public void addDate(String key, long value) {
		add(key, Https.httpDate(new Date(value)));
	}
	public void add(String key, String value) {
		if (datas.containsKey(key))
			datas.get(key).add(value);
		else {
			List<String> hs = new ArrayList<String>();
			hs.add(value);
			datas.put(key, hs);
		}
	}
	public void addInt(String key, int value) {
		add(key, ""+value);
	}
	public boolean contains(String key) {
		return datas.containsKey(key);
	}
	public void setDate(String key, long value) {
		set(key, Https.httpDate(new Date(value)));
	}
	public void setInt(String key, int value) {
		set(key, ""+value);
	}
	public void set(String key, String value) {
		List<String> hs = datas.get(key);
		if (hs == null) {
			hs = new ArrayList<String>();
			hs.add(value);
			datas.put(key, hs);
		} else {
			hs.clear();
			hs.add(value);
		}
	}
}
