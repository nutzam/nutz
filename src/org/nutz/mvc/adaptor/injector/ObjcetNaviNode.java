package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.lang.Mirror;
import org.nutz.lang.inject.Injecting;
import org.nutz.mvc.adaptor.ParamConvertor;
import org.nutz.mvc.adaptor.Params;

/**
 * request对象导航注入节点树
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author juqkai (juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
class ObjcetNaviNode {
	private static final char separator = '.';
	//节点名
	private String name;
	//叶子节点的值
	private String[] value;
	//是否是叶子节点
	private boolean leaf = true;
	//子节点
	private Map<String, ObjcetNaviNode> child = new HashMap<String, ObjcetNaviNode>();

	/**
	 * 初始化当前结点
	 * 
	 */
	public void put(String path, String[] value) {
	    path = path.replace('[', '.');
	    path = path.replace("]", "");
		name = fetchName(path);
		String subPath = path.substring(path.indexOf(separator) + 1); 
		if (path.indexOf(separator) <= 0 || "".equals(subPath)) {
			this.value = value;
			return;
		}
		leaf = false;
		addChild(subPath, value);
	}

	/**
	 * 添加子结点
	 * 
	 */
	private void addChild(String path, String[] value) {
		String subname = fetchName(path);
		ObjcetNaviNode onn = child.get(subname);
		if (onn == null) {
			onn = new ObjcetNaviNode();
		}
		onn.put(path, value);
		child.put(subname, onn);
	}
	
	/**
	 * 取得节点名
	 * 
	 */
	private String fetchName(String path) {
		if (path.indexOf(separator) <= 0) {
			return path;
		}
		return path.substring(0, path.indexOf(separator));
	}
	
	/**
	 * 将结点树中的值注入到 mirror 中
	 * 
	 * @param mirror
	 *            待注入对象
	 */
	public Object inject(Mirror<?> mirror) {
	    // TODO 这里的几个实现, 感觉可以把它们提成单独的类来实现.
	    if(mirror.is(List.class)){
	        return injectList(mirror);
	    } else if(mirror.is(Map.class)){
	        return injectMap(mirror);
	    } else if(mirror.is(Set.class)){
	        return injectSet(mirror);
	    }
        return injectObj(mirror);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object injectMap(Mirror<?> mirror){
	    Map obj = new HashMap();
	    for (Entry<String, ObjcetNaviNode> entry : child.entrySet()) {
	        ObjcetNaviNode onn = entry.getValue();
	        if (onn.isLeaf()) {
	            Class<?> clazz = (Class<?>) mirror.getGenericsType(0);
	            ParamConvertor pc = Params.makeParamConvertor(clazz);
	            obj.put(entry.getKey(), pc.convert(onn.getValue()));
	            continue;
	        }
	        // 不是叶子结点,不能直接注入
	        Mirror<?> fieldMirror = Mirror.me(mirror.getGenericsType(1));
	        obj.put(entry.getKey(), onn.inject(fieldMirror));
	    }
	    return obj;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private Object injectSet(Mirror<?> mirror){
	    return injectCollection(new HashSet(), mirror);
	}
	@SuppressWarnings("rawtypes")
    private Object injectList(Mirror<?> mirror){
	    return injectCollection(new ArrayList(), mirror);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private Object injectCollection(Collection obj, Mirror mirror){
	    for (Entry<String, ObjcetNaviNode> entry : child.entrySet()) {
            ObjcetNaviNode onn = entry.getValue();
            if (onn.isLeaf()) {
                Class<?> clazz = (Class<?>) mirror.getGenericsType(0);
                ParamConvertor pc = Params.makeParamConvertor(clazz);
                obj.add(pc.convert(onn.getValue()));
                continue;
            }
            // 不是叶子结点,不能直接注入
            Mirror<?> fieldMirror = Mirror.me(mirror.getGenericsType(0));
            obj.add(onn.inject(fieldMirror));
        }
        return obj;
	}
	private Object injectObj(Mirror<?> mirror){
	    Object obj = mirror.born();
	    for (Entry<String, ObjcetNaviNode> entry : child.entrySet()) {
            ObjcetNaviNode onn = entry.getValue();
            Injecting in = mirror.getInjecting(entry.getKey());
            if (onn.isLeaf()) {
                try {
                    ParamConvertor pc = Params.makeParamConvertor(mirror.getField(entry.getKey()).getType());
                    in.inject(obj, pc.convert(onn.getValue()));
                } catch (NoSuchFieldException e) {
                    continue;
                }
                continue;
            }
            // 不是叶子结点,不能直接注入
            try {
                Type type = mirror.getField(entry.getKey()).getGenericType();
                Mirror<?> fieldMirror = Mirror.me(type);
                in.inject(obj, onn.inject(fieldMirror));
            }
            catch (NoSuchFieldException e) {
                continue;//TODO 是不是应该log一下呢?
            }
        }
	    return obj;
	}

	public String getName() {
		return name;
	}

	public String[] getValue() {
		return value;
	}

	public boolean isLeaf() {
		return leaf;
	}
}
