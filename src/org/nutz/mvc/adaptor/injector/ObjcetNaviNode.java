package org.nutz.mvc.adaptor.injector;

import java.util.HashMap;
import java.util.Map;
import org.nutz.lang.Mirror;
import org.nutz.lang.inject.Injecting;

/**
 * request对象导航注入节点树
 * 
 * @author juqkai (juqkai@gmail.com) 2011-1-28
 */
class ObjcetNaviNode {
	private char separator = '.';
	private String name;
	private Object value;
	private boolean leaf = true;
	private Map<String, ObjcetNaviNode> child = new HashMap<String, ObjcetNaviNode>();

	/**
	 * 初始化当前结点
	 * 
	 * @author juqkai (juqkai@gmail.com)
	 */
	public void put(String path, Object value) {
		name = fetchName(path);
		if (path.indexOf(separator) <= 0) {
			this.value = value;
			return;
		}
		leaf = false;
		addChild(path.substring(path.indexOf(separator) + 1), value);
	}

	/**
	 * 添加子结点
	 * 
	 * @author juqkai (juqkai@gmail.com)
	 */
	private void addChild(String path, Object value) {
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
	 * @author juqkai (juqkai@gmail.com)
	 */
	private String fetchName(String path) {
		if (path.indexOf(separator) <= 0) {
			return path;
		}
		return path.substring(0, path.indexOf(separator));
	}

	/**
	 * 将结点树中的值注入到 mirror 中
	 * @param mirror 待注入对象
	 * @return
	 */
	public Object inject(Mirror<?> mirror) {
		Object obj = mirror.born();
		for (String key : child.keySet()) {
			ObjcetNaviNode onn = child.get(key);
				Injecting in = mirror.getInjecting(key);
				if (onn.isLeaf()) {
					in.inject(obj, onn.getValue());
					continue;
				}
				// 不是叶子结点,不能直接注入
				Mirror<?> fieldMirror;
				try {
					fieldMirror = Mirror.me(mirror.getField(key).getType());
					in.inject(obj, onn.inject(fieldMirror));
				} catch (NoSuchFieldException e) {
					continue;
				}
		}
		return obj;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public boolean isLeaf() {
		return leaf;
	}
}
