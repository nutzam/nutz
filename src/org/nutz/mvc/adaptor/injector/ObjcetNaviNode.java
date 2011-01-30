package org.nutz.mvc.adaptor.injector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.lang.Mirror;
import org.nutz.lang.inject.Injecting;

/**
 * request对象导航注入节点树
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author juqkai (juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
class ObjcetNaviNode {
	private static final char separator = '.';
	private String name;
	private Object value;
	private boolean leaf = true;
	private Map<String, ObjcetNaviNode> child = new HashMap<String, ObjcetNaviNode>();

	/**
	 * 初始化当前结点
	 * 
	 */
	public void put(String path, Object value) {
		name = fetchName(path);
		if (path.indexOf(separator) <= 0) {
			this.value = value;
			return;
		}
		leaf = false;
		//TODO 如果最后一个字符为'.',会报错,是否应该处理?
		addChild(path.substring(path.indexOf(separator) + 1), value);
	}

	/**
	 * 添加子结点
	 * 
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
		Object obj = mirror.born();
		for (Entry<String, ObjcetNaviNode> entry : child.entrySet()) {
			ObjcetNaviNode onn = entry.getValue();
			Injecting in = mirror.getInjecting(entry.getKey());
			if (onn.isLeaf()) {
				in.inject(obj, onn.getValue());
				continue;
			}
			// 不是叶子结点,不能直接注入
			Mirror<?> fieldMirror;
			try {
				fieldMirror = Mirror.me(mirror.getField(entry.getKey()).getType());
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

	public Object getValue() {
		return value;
	}

	public boolean isLeaf() {
		return leaf;
	}
}
