package org.nutz.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class Header {

    private Header() {
        items = new HashMap<String, String>();
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
    
    public Set<Entry<String, String>> getAll(){
        return items.entrySet();
    }

    public Header addAll(Map<String, String> map) {
        if (null != map)
            items.putAll(map);
        return this;
    }

    @Override
    public String toString() {
        return Json.toJson(items, JsonFormat.nice().setIgnoreNull(false));
    }

    public static Header create(Map<String, String> properties) {
        return new Header().addAll(properties);
    }

    @SuppressWarnings("unchecked")
    public static Header create(String properties) {
        return create((Map<String, String>) Json.fromJson(properties));
    }

    public static Header create() {
        Header header = new Header();
        header.set("User-Agent", "Nutz.Robot");
        header.set("Accept-Encoding", "gzip,deflate");
        header.set("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;"
                                + "q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        header.set("Accept-Language", "en-US,en,zh,zh-CN");
        header.set("Accept-Charset", "ISO-8859-1,*,utf-8");
        header.set("Connection", "keep-alive");
        header.set("Cache-Control", "max-age=0");
        return header;
    }

    public String get(String key, String defaultValue) {
    	String value = get(key);
    	if (value == null)
    		return defaultValue;
    	return value;
    }
    
    public int getInt(String key, int defaultValue) {
    	String value = get(key);
    	if (value == null)
    		return defaultValue;
    	return Integer.parseInt(value);
    }
}
