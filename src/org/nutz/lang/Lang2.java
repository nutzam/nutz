package org.nutz.lang;

import java.lang.reflect.Array;
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
        if(List.class.isAssignableFrom(me.getType())){
            return injectList(model, me);
        } else if(Map.class.isAssignableFrom(me.getType())){
            return injectMap(model, me);
//        } else if(me.is(Set.class)){
//            return injectSet(me);
        } else if(me.getType().isArray()){
            return injectArray(model, me);
        }
        return injectObj(model, me);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object injectArray(Object model, Mirror<?> me){
        Class<?> clazz = me.getType().getComponentType();
        List list = (List) model;
        List vals = new ArrayList();
        for(Object obj : list){
            if(isLeaf(obj)){
                vals.add(Castors.me().castTo(obj, clazz));
                continue;
            }
            vals.add(inject(obj, clazz));
        }
        Object obj = Array.newInstance(clazz, vals.size());
        for(int i = 0; i < vals.size(); i++){
            Array.set(obj, i, vals.get(i));
        }
        return obj;
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
        if(me.getGenericsTypes() == null){
            re.putAll(map);
            return re;
        }
        
        for(Object key : map.keySet()){
            Object val = map.get(key);
            if(isLeaf(val)){
                re.put(key, Castors.me().castTo(val, fetchGenericsType(me, 1)));
                continue;
            }
            re.put(key, inject(val, fetchGenericsType(me, 1)));
        }
        return re;
    }
    
    private static Class<?> fetchGenericsType(Mirror<?> me, int index){
        return Lang.getTypeClass(me.getGenericsType(index));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object injectList(Object model, Mirror<?> me){
        List re = null;
        if(me.isInterface()){
            re = new ArrayList();
        } else {
            re = (List) me.born();
        }
        if(me.getGenericsTypes() == null){
            return model;
        }
        
        for(Object obj : (List) model){
            if(isLeaf(obj)){
                re.add(Castors.me().castTo(obj, fetchGenericsType(me, 0)));
                continue;
            }
            re.add(inject(obj, fetchGenericsType(me, 0)));
        }
        return re;
    }
    
    @SuppressWarnings("unchecked")
    private static Object injectObj(Object model, Mirror<?> me) {
        Object obj = me.born();
        Map<String, ?> map = (Map<String, ?>) model;
        for(String key : map.keySet()){
            try{
                Object val = map.get(key);
                Injecting in = me.getInjecting(key);
                if(isLeaf(val)){
                    Type t = Lang.getFieldType(me, key);
                    in.inject(obj, Castors.me().castTo(val, Lang.getTypeClass(t)));
                    continue;
                }
                in.inject(obj, inject(val, Lang.getFieldType(me, key)));
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
