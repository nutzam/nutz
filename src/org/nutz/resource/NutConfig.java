package org.nutz.resource;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Objs;
import org.nutz.resource.impl.FileResource;

public class NutConfig {
    @SuppressWarnings("rawtypes")
    private Map<?, ?> map = new HashMap();
    
    private static NutConfig me;
    private NutConfig(){}

    public static void load(String... paths) {
        if(null == me){
            me = new NutConfig();
        }
        me.loadResource(paths);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void loadResource(String... paths){
        for(String path : paths){
            List<NutResource> resources;
            if(path.endsWith(".js")){
                File f = Files.findFile(path);
                resources = new ArrayList<NutResource>();
                resources.add(new FileResource(f));
            } else {
                resources = Scans.me().scan(path, "\\.js$");
            }
            
            for(NutResource nr : resources){
                try {
                    Object obj = Json.fromJson(nr.getReader());
                    if(obj instanceof Map){
                        Map m = (Map) obj;
                        map.putAll(m);
                        for(Object key : m.keySet()){
                            if(key.equals("include")){
                                List<String> include = (List) m.get("include");
                                loadResource(include.toArray(new String[0]));
                            }
                        }
                    }
                } catch (Exception e) {}
            }
        }
    }
    
    public static Object get(String key, Type type) {
        return me.getItem(key, type);
    }
    
    public static Object get(String key){
        return me.getItem(key, null);
    }

    private Object getItem(String key, Type type){
        if(null == map){
            return null;
        }
        if(null == type){
            return map.get(key);
        }
        return Objs.convert(map.get(key), type);
    }
}
