package org.nutz.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class JsonMerge {
    public static Object merge(Object... objs){
        if(objs == null || objs.length == 0){
            return null;
        }
        if(objs.length == 1){
            return objs[0];
        }
        //判断是否兼容
        Class<?> clazz = objs[0].getClass();
        for(int i = 1; i < objs.length; i ++){
            if(!objs[i].getClass().isAssignableFrom(clazz)){
                throw new JsonException(0, 0, '0', "类型不兼容, 无法进行合并!");
            }
        }
        if(objs[0] instanceof Map){
            return mergeMap(objs);
        }
        if(objs[0] instanceof List){
            return mergeList(objs);
        }
            
        return null;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object mergeList(Object... objs) {
        List list = new ArrayList();
        for(Object li : objs){
            List src = (List) li;
            for(Object obj : src){
                if(!list.contains(obj)){
                    list.add(obj);
                }
            }
        }
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object mergeMap(Object... objs){
        Map obj = new HashMap();
        for(int i = 0; i < objs.length; i ++){
            Map map = (Map) objs[i];
            for(Object key : map.keySet()){
                Object objval = obj.get(key);
                Object val = map.get(key);
                if(objval != null && (val instanceof List || val instanceof Map)){
                    val = merge(objval, val);
                }
                obj.put(key, val);
            }
        }
        return obj;
    }
}
