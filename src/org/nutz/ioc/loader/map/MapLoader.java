package org.nutz.ioc.loader.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 从一个 Map 对象中读取配置信息，支持 Parent
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class MapLoader implements IocLoader {
    
    private static final Log log = Logs.get();

    private Map<String, Map<String, Object>> map;

    protected MapLoader() {
        map = new HashMap<String, Map<String, Object>>();
    }

    @SuppressWarnings("unchecked")
    public MapLoader(String json) {
        this((Map<String, Map<String, Object>>) Json.fromJson(json));
    }

    public MapLoader(Map<String, Map<String, Object>> map) {
        this.map = map;
    }

    public Map<String, Map<String, Object>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, Object>> map) {
        this.map = map;
    }

    public String[] getName() {
        return map.keySet().toArray(new String[map.size()]);
    }

    public boolean has(String name) {
        return map.containsKey(name);
    }

    /**
     * {@link ObjectLoadException}
     */
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        Map<String, Object> m = getMap(name);
        if (null == m)
            throw new ObjectLoadException("Object '" + name + "' without define!");
        if(log.isDebugEnabled())
            log.debug("Loading define for name="+name);
        // If has parent
        Object p = m.get("parent");
        if (null != p) {
            checkParents(name);
            IocObject parent = load(loading, p.toString());
            // create new map without parent
            Map<String, Object> newMap = new HashMap<String, Object>();
            for (Entry<String, Object> en : m.entrySet()) {
                if ("parent".equals(en.getKey()))
                    continue;
                newMap.put(en.getKey(), en.getValue());
            }
            // Create self IocObject
            IocObject self = loading.map2iobj(newMap);

            // Merge with parent
            return Iocs.mergeWith(self, parent);
        }
        return loading.map2iobj(m);
    }

    /**
     * 检查继承关系,如果发现循环继承,或其他错误的继承关系,则抛出ObjectLoadException
     * 
     * @param name
     *            beanId
     * @throws ObjectLoadException
     *             if Inheritance errors or Inheritance cycle founded.
     */
    @SuppressWarnings("unchecked")
    private void checkParents(String name) throws ObjectLoadException {
        ArrayList<String> list = new ArrayList<String>();
        list.add(name);
        String currentParent = map.get(name).get("parent").toString();
        while (true) {
            if (currentParent == null)
                break;
            if (list.contains(currentParent))
                throw Lang.makeThrow(    ObjectLoadException.class,
                                        "!!!Inheritance cycle! id = %s",
                                        name);
            list.add(currentParent);
            Object obj = map.get(currentParent);
            if (obj != null && obj instanceof Map)
                currentParent = (String) ((Map<String, Object>) obj).get("parent");
            else
                throw Lang.makeThrow(    ObjectLoadException.class,
                                        "!!!Inheritance errors! id = %s",
                                        name);
        }
    }

    /**
     * Inner Object can not support 'parent'.
     * 
     * @param name
     *            object Map name
     * @return object Map
     */
    private Map<String, Object> getMap(String name) {
        return map.get(name);
    }

    @Override
    public String toString() {
        return "/*" + getClass().getSimpleName() + "*/\n" + Json.toJson(map);
    }
}
