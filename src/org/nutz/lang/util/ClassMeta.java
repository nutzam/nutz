package org.nutz.lang.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;

public class ClassMeta {

    public String type;
    public Map<String, List<String>> paramNames = new HashMap<String, List<String>>();
    public Map<String, Integer> methodLines = new HashMap<String, Integer>();
    
    @Override
    public String toString() {
        NutMap map = new NutMap();
        map.put("type", type);
        map.put("paramNames", paramNames);
        map.put("methodLines", methodLines);
        return Json.toJson(map);
    }
}
