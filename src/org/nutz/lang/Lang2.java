package org.nutz.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.inject.Injecting;

public class Lang2 {
    public static Object inject(Object model, Type type){
        Mirror<?> me = Mirror.me(type);
        if(me.is(List.class)){
            return injectList(model, me);
        } else if(me.is(Map.class)){
            return injectMap(model, me);
//        } else if(me.is(Set.class)){
//            return injectSet(me);
//        } else if(me.getType().isArray()){
//            return injectArray(me);
        }
        return injectObj(model, me);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object injectMap(Object model, Mirror<?> me){
        Map re = null;
        if(me.isInterface()){
            re = new HashMap();
        } else {
            re = (Map) me.born();
        }
        
        Map map = (Map) model;
        for(Object key : map.keySet()){
            Object val = map.get(key);
            if(isLeaf(val)){
                re.put(key, Castors.me().castTo(val, (Class<?>) me.getGenericsType(1)));
            }
            re.put(key, inject(val, me.getGenericsType(1)));
        }
        return re;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object injectList(Object model, Mirror<?> me){
        List re = null;
        if(me.isInterface()){
            re = new ArrayList();
        } else {
            re = (List) me.born();
        }
        
        for(Object obj : (List) model){
            if(isLeaf(obj)){
                re.add(Castors.me().castTo(obj, (Class<?>) me.getGenericsType(0)));
                continue;
            }
            re.add(inject(obj, me.getGenericsType(0)));
        }
        return re;
    }
    
    @SuppressWarnings("unchecked")
    private static Object injectObj(Object model, Mirror<?> me) {
        Object obj = me.born();
        Map<String, ?> map = (Map<String, ?>) model;
        for(String key : map.keySet()){
            try{
                Field field = me.getField(key);
                Object val = map.get(key);
                Injecting in = me.getInjecting(key);
                if(isLeaf(val)){
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
    
    private static boolean isLeaf(Object obj){
        if(obj instanceof Map){
            return false;
        }
        if(obj instanceof List){
            return false;
        }
        return true;
    }
}
