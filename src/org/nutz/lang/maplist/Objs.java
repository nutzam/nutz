package org.nutz.lang.maplist;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.nutz.castor.Castors;
import org.nutz.el.El;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Context;

/**
 * 集合了对象转换合并等高级操作
 * 
 * @author juqkai(juqkai@gmail.com)
 * 
 */
public class Objs {

	/**
	 * 转换器中间对象合并器<br/>
	 * 合并 {@link Objs} 中定义的中间结构.<br/>
	 * 规则:<br>
	 * <ul>
	 * <li>普通对象, 保存为List, 但是要去掉重复.
	 * <li>合并 map , 如果 key 值相同, 那么后一个值覆盖前面的值.递归合并
	 * <li>list不做递归合并, 只做简单的合并, 清除重复的操作.
	 * </ul>
	 */
	public static Object merge(Object... objs) {
		if (objs == null || objs.length == 0) {
			return null;
		}
		if (objs.length == 1) {
			return objs[0];
		}
		// @ TODO 这里要不要判断是否兼容呢?
		if (objs[0] instanceof Map) {
			return mergeMap(objs);
		}
		if (objs[0] instanceof List) {
			return mergeList(objs);
		}
		return mergeObj(objs);
	}

	/**
	 * 对象合并
	 * 
	 * @param objs
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Object mergeObj(Object[] objs) {
		List list = new ArrayList();
		for (Object obj : objs) {
			if (list.contains(obj)) {
				continue;
			}
			list.add(obj);
		}
		return list;
	}

	/**
	 * list合并
	 * 
	 * @param objs
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Object mergeList(Object... objs) {
		List list = new ArrayList();
		for (Object li : objs) {
			List src = (List) li;
			for (Object obj : src) {
				if (!list.contains(obj)) {
					list.add(obj);
				}
			}
		}
		return list;
	}

	/**
	 * map合并
	 * 
	 * @param objs
	 * @return
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Object mergeMap(Object... objs) {
		Map obj = new HashMap();
		for (int i = 0; i < objs.length; i++) {
			Map map = (Map) objs[i];
			for (Object key : map.keySet()) {
				Object objval = obj.get(key);
				Object val = map.get(key);
				if (objval != null && (val instanceof List || val instanceof Map)) {
					val = merge(objval, val);
				}
				obj.put(key, val);
			}
		}
		return obj;
	}
	
	
	//路径
    Stack<String> path = new Stack<String>();
    //对象缓存
    Context context = Lang.context();
    public Objs(){
        
    }

	/**
	 * 这个实现, 主要将 List, Map 的对象结构转换成真实的对象.
	 * <p>
	 * 规则:
	 * <ul>
	 * <li>对象以Map存储, key为属性名, value为属性值
	 * <li>数组以List存储
	 * <li>Map直接存储为Map
	 * <li>List直接存储为List
	 * <li>只要不是List, Map 存储的, 都认为是可以直接写入对象的. TODO 这点可以调整一下.
	 * </ul>
	 */
	public static Object convert(Object model, Type type) {
	    if (model == null)
            return null;
        if (type == null)
            return model;
        // obj是基本数据类型或String
        if (!(model instanceof Map) && !(model instanceof List)) {
            return Castors.me().castTo(model, Lang.getTypeClass(type));
        }
        
        return new Objs().inject(model, type);
	}
	
	
	

    public Object inject(Object model, Type type){
        if(model == null){
            return null;
        }
        Mirror<?> me = Mirror.me(type);
        Object obj = null;
        if(Collection.class.isAssignableFrom(me.getType())){
            obj = injectCollection(model, me);
        } else if(Map.class.isAssignableFrom(me.getType())){
            obj = injectMap(model, me);
        } else if(me.getType().isArray()){
            obj = injectArray(model, me);
        } else {
            obj= injectObj(model, me);
        }
        if(path.size() > 0)
            path.pop();
        return obj;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object injectArray(Object model, Mirror<?> me){
        Class<?> clazz = me.getType().getComponentType();
        List list = (List) model;
        List vals = new ArrayList();
        int j = 0;
        for(Object obj : list){
            if(isLeaf(obj)){
                vals.add(Castors.me().castTo(obj, clazz));
                continue;
            }
            path.push("a"+(j++));
            vals.add(inject(obj, clazz));
        }
        Object obj = Array.newInstance(clazz, vals.size());
        for(int i = 0; i < vals.size(); i++){
            Array.set(obj, i, vals.get(i));
        }
        return obj;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object injectMap(Object model, Mirror<?> me){
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
            path.push(key.toString());
            re.put(key, inject(val, type));
        }
        return re;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object injectCollection(Object model, Mirror<?> me){
        Collection re = null;
        if(!me.isInterface()){
            re =  (Collection) me.born();
        } else {
            re = makeCollection(me);
        }
        if(me.getGenericsTypes() == null){
        	re.addAll((Collection) model);
            return re;
        }
        Type type = me.getGenericsType(0);
        int j = 0;
        for(Object obj : (Collection) model){
            if(isLeaf(obj)){
                re.add(Castors.me().castTo(obj, Lang.getTypeClass(type)));
                continue;
            }
            path.push("a"+(j++));
            re.add(inject(obj, type));
        }
        return re;
    }
    
    @SuppressWarnings("rawtypes")
    private Collection makeCollection(Mirror<?> me) {
        if(List.class.isAssignableFrom(me.getType())){
            return new ArrayList();
        }
        if(Set.class.isAssignableFrom(me.getType())){
            return new HashSet();
        }
        throw new RuntimeException("不支持的类型!");
    }

    @SuppressWarnings("unchecked")
    private Object injectObj(Object model, Mirror<?> me){
        Object obj = me.born();
        context.set(fetchPath(), obj);
        Map<String, ?> map = (Map<String, ?>) model;
        
        for(String key : map.keySet()){
            JsonEntityField jef = JsonEntityField.eval(me, key);
            if(jef == null){
                continue;
            }
            
            Object val = map.get(jef.getName());
            if(val == null){
                continue;
            }
            
            if(isLeaf(val)){
                if(val instanceof El){
                    val = ((El)val).eval(context);
                }
                jef.setValue(obj, Castors.me().castTo(jef.createValue(obj, val), Lang.getTypeClass(jef.getGenericType())));
                continue;
            }
            path.push(key);
            Object o = jef.createValue(obj, inject(val, jef.getGenericType()));
            jef.setValue(obj, o);
        }
        return obj;
    }
    
    private boolean isLeaf(Object obj){
        if(obj instanceof Map){
            return false;
        }
        if(obj instanceof List){
            return false;
        }
        return true;
    }
    
    private String fetchPath(){
        StringBuffer sb = new StringBuffer();
        sb.append("root");
        for(String item : path){
            if(item.charAt(0) != 'a'){
                sb.append("m");
            }
            sb.append(item);
        }
        return sb.toString();
    }
    
    //-------------------------------------提取数据-----------------------------------------
    /**
     * 访问MAP, List结构的数据, 通过 uers[2].name 这种形式.
     */
    public static Object cell(Object obj, String path){
        return MapListCell.cell(obj, path);
    }
    
}
