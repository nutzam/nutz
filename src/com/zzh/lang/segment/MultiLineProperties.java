package com.zzh.lang.segment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zzh.lang.Strings;
import com.zzh.lang.stream.CharInputStream;

public class MultiLineProperties implements Map<String, String> {
	public MultiLineProperties(InputStream ins, String encoding) throws IOException {
		maps = new HashMap<String, String>();
		keys = new LinkedList<String>();
		this.encoding = encoding;
		load(ins);
	}

	public MultiLineProperties(InputStream ins) throws IOException {
		this(ins, null);
	}

	public MultiLineProperties() {
		maps = new HashMap<String, String>();
		keys = new LinkedList<String>();
		this.encoding = null;
	}

	public MultiLineProperties(CharSequence cs) throws IOException {
		this(new CharInputStream(cs));
	}

	public MultiLineProperties(MultiLineProperties p) {
		this();
		maps.putAll(p.maps);
		keys.addAll(p.keys);
	}

	public MultiLineProperties(java.util.Properties p) {
		maps = new HashMap<String, String>();
		keys = new LinkedList<String>();
		Enumeration<?> en = p.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			String v = (String) p.get(key);
			maps.put(key, v);
		}
	}

	protected Map<String, String> maps;
	protected String encoding;
	protected List<String> keys;

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public synchronized void load(InputStream ins) throws IOException {
		BufferedReader tr = new BufferedReader((null == encoding ? new InputStreamReader(ins)
				: new InputStreamReader(ins, encoding)));
		this.clear();
		String s;
		while (null != (s = tr.readLine())) {
			if (Strings.isBlank(s))
				continue;
			if (s.startsWith("#"))
				continue;
			int pos;
			char c = '0';
			for (pos = 0; pos < s.length(); pos++) {
				c = s.charAt(pos);
				if (c == '=' || c == ':')
					break;
			}
			if (c == '=') {
				String name = s.substring(0, pos);
				maps.put(name, s.substring(pos + 1));
				keys.add(name);
			} else if (c == ':') {
				String name = s.substring(0, pos);
				StringBuffer sb = new StringBuffer();
				sb.append(s.substring(pos + 1));
				String ss;
				while (null != (ss = tr.readLine())) {
					if (ss.startsWith("#"))
						break;
					sb.append("\r\n" + ss);
				}
				maps.put(name, sb.toString());
				keys.add(name);
				if (null == ss)
					return;
			} else {
				maps.put(s, null);
				keys.add(s);
			}
		}
	}

	public synchronized String setProperty(String key, String value) {
		return maps.put(key, value);
	}

	public String getString(String key) {
		Object v = maps.get(key);
		if (null == v)
			return null;
		return v.toString();
	}

	public String getString(String key, String defaultValue) {
		String v = getString(key);
		return (null == v ? defaultValue : v);
	}

	public String getStringTrimed(String key) {
		return Strings.trim(this.getString(key));
	}

	public String getStringTrimed(String key, String defaultValue) {
		return Strings.trim(this.getString(key, defaultValue));
	}

	public int getInt(String key) {
		Object obj = get(key);
		if (null != obj)
			if (obj instanceof Integer)
				return ((Integer) obj).intValue();
			else
				return Integer.parseInt(obj.toString());
		return -1;
	}

	public float getFloat(String key) {
		Object obj = get(key);
		if (null != obj)
			if (obj instanceof Float)
				return ((Float) obj).floatValue();
			else
				return Float.parseFloat(obj.toString());
		return -1;
	}

	public boolean getBoolean(String key) {
		Object obj = get(key);
		if (null != obj)
			if (obj instanceof Float)
				return ((Boolean) obj).booleanValue();
			else
				return Boolean.parseBoolean(obj.toString());
		return false;
	}

	public long getLong(String key) {
		Object obj = get(key);
		if (null != obj)
			if (obj instanceof Float)
				return ((Long) obj).longValue();
			else
				return Long.parseLong(obj.toString());
		return -1L;
	}

	public double getDouble(String key) {
		Object obj = get(key);
		if (null != obj)
			if (obj instanceof Float)
				return ((Double) obj).doubleValue();
			else
				return Double.parseDouble(obj.toString());
		return -1;
	}

	public synchronized void clear() {
		maps.clear();
	}

	public boolean containsKey(Object key) {
		return maps.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return maps.containsValue(value);
	}

	public Set<Entry<String, String>> entrySet() {
		return maps.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return maps.equals(o);
	}

	@Override
	public int hashCode() {
		return maps.hashCode();
	}

	public boolean isEmpty() {
		return maps.isEmpty();
	}

	public Set<String> keySet() {
		return maps.keySet();
	}

	public List<String> keys() {
		return keys;
	}

	public synchronized String put(String key, String value) {
		return maps.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public synchronized void putAll(Map t) {
		maps.putAll(t);
	}

	public synchronized String remove(Object key) {
		return maps.remove(key);
	}

	public int size() {
		return maps.size();
	}

	public Collection<String> values() {
		return maps.values();
	}

	public String get(Object key) {
		return maps.get(key);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new MultiLineProperties(this);
	}

}
