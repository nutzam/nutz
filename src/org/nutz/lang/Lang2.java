package org.nutz.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.castor.Castors;
import org.nutz.lang.inject.Injecting;

public class Lang2 {
    public static Object inject(Object model, Type type){
        Mirror<?> me = Mirror.me(type);
//        if(me.is(List.class)){
//            return injectList(me);
//        } else if(me.is(Map.class)){
//            return injectMap(me);
//        } else if(me.is(Set.class)){
//            return injectSet(me);
//        } else if(me.getType().isArray()){
//            return injectArray(me);
//        }
        return injectObj(model, me);
    }
    
    @SuppressWarnings({ "unused", "rawtypes" })
    private static Object injectList(Object model, Mirror<?> me){
        Map re = null;
        if(me.isInterface()){
            re = new HashMap();
        } else {
            re = (Map) me.born();
        }
        return null;
    }
    
    private static Object injectObj(Object model, Mirror<?> me) {
        Object obj = me.born();
        Map<String, ?> map = (Map<String, ?>) model;
        for(String key : map.keySet()){
            try{
                Field field = me.getField(key);
                Object val = map.get(key);
                Injecting in = me.getInjecting(key);
                if(!(val instanceof Map || val instanceof List)){
                    in.inject(obj, Castors.me().castTo(val, field.getType()));
                    continue;
                }
                in.inject(obj, inject(val, field.getGenericType()));
            } catch (NoSuchFieldException e){
                continue;
            }
        }
        return obj;
    }
    
}
