package org.nutz.mvc.adaptor.injector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		Object obj = mirror.born();
		for (Entry<String, ObjcetNaviNode> entry : child.entrySet()) {
			ObjcetNaviNode onn = entry.getValue();
			Injecting in = mirror.getInjecting(entry.getKey());
			if (onn.isLeaf()) {
				try {
					ParamConvertor pc = Params.makeParamConvertor(mirror.getField(entry.getKey()).getType());
					in.inject(obj, pc.convert(onn.getValue()));
	//				in.inject(obj, onn.getValue());
				} catch (NoSuchFieldException e) {
					continue;
				}
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

	public String[] getValue() {
		return value;
	}

	public boolean isLeaf() {
		return leaf;
	}
}
