package org.nutz.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.castor.Castors;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.inject.Injecting;

/**
 * 对象转换器.<br/>
 * 这个实现, 主要将 List, Map 的对象结构转换成真实的对象.<br/>
 * 规则:<br/>
 * <ul>
 *  <li>对象以Map存储, key为属性名, value为属性值
 *  <li>数组以List存储
 *  <li>Map直接存储为Map
 *  <li>List直接存储为List
 *  <li>只要不是List, Map 存储的, 都认为是可以直接写入对象的. TODO 这点可以调整一下.
 * </ul>
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Parsing {
    
    public static Object convert(Object model, Type type) {
        if (model == null)
            return null;
        if (type == null)
            return model;
        // obj是基本数据类型或String
        if (!(model instanceof Map) && !(model instanceof List)) {
            return Castors.me().castTo(model, Lang.getTypeClass(type));
        }
        
        return inject(model, type);
    }
    
    public static Object inject(Object model, Type type){
        Mirror<?> me = Mirror.me(type);
        if(Collection.class.isAssignableFrom(me.getType())){
            return injectCollection(model, me);
        } else if(Map.class.isAssignableFrom(me.getType())){
            return injectMap(model, me);
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
        
        Type type = me.getGenericsType(1);
        for(Object key : map.keySet()){
            Object val = map.get(key);
            //转换Key
            if(!isLeaf(key)){
                key = inject(key, me.getGenericsType(0));
            }
            //转换val并填充
            if(isLeaf(val)){
                re.put(key, Castors.me().castTo(val, Lang.getTypeClass(type)));
                continue;
            }
            re.put(key, inject(val, type));
        }
        return re;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object injectCollection(Object model, Mirror<?> me){
        Collection re = null;
        if(!me.isInterface()){
            re =  (Collection) me.born();
        } else {
            re = makeCollection(me);
        }
        if(me.getGenericsTypes() == null){
            return model;
        }
        Type type = me.getGenericsType(0);
        for(Object obj : (Collection) model){
            if(isLeaf(obj)){
                re.add(Castors.me().castTo(obj, Lang.getTypeClass(type)));
                continue;
            }
            re.add(inject(obj, type));
        }
        return re;
    }
    
    @SuppressWarnings("rawtypes")
    private static Collection makeCollection(Mirror<?> me) {
        if(List.class.isAssignableFrom(me.getType())){
            return new ArrayList();
        }
        if(Set.class.isAssignableFrom(me.getType())){
            return new HashSet();
        }
        throw new RuntimeException("不支持的类型!");
    }

    @SuppressWarnings("unchecked")
    private static Object injectObj(Object model, Mirror<?> me){
        Object obj = me.born();
        Map<String, ?> map = (Map<String, ?>) model;
        for(Field field : me.getFields()){
            JsonEntityField jef = JsonEntityField.eval(me, field);
            Object val = map.get(jef.getName());
            if(val == null){
                continue;
            }
            
            Injecting in = me.getInjecting(field.getName());
            if(isLeaf(val)){
                Type t = Lang.getFieldType(me, field);
                in.inject(obj, Castors.me().castTo(jef.createValue(obj, val), Lang.getTypeClass(t)));
                continue;
            }
            in.inject(obj, jef.createValue(obj, inject(val, Lang.getFieldType(me, field))));
            
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
