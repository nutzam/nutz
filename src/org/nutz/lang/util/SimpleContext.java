package org.nutz.lang.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 可以用来存储无序名值对
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SimpleContext extends AbstractContext {

    private Map<String, Object> map;

    public SimpleContext() {
        this(new HashMap<String, Object>());
    }

    public SimpleContext(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Context set(String name, Object value) {
        map.put(name, value);
        return this;
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public boolean has(String key) {
        return map.containsKey(key);
    }

    @Override
    public Map<String, Object> getInnerMap() {
        return map;
    }

    @Override
    public Context clear() {
        this.map.clear();
        return this;
    }

    @Override
    public Object get(String name) {
        return map.get(name);
    }

    @Override
    public SimpleContext clone() {
        SimpleContext context = new SimpleContext();
        context.map.putAll(this.map);
        return context;
    }

    @Override
    public String toString() {
        return Json.toJson(map, JsonFormat.nice());
    }
}
