package org.nutz.ioc.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.lang.Lang;

/**
 * 自定义级别上下文对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LevelContext implements IocContext {

	private String level;
	private Map<String, ObjectProxy> objs;

	public LevelContext(String level) {
		this.level = level;
		objs = new HashMap<String, ObjectProxy>();
	}

	private void checkBuffer() {
		if (null == objs)
			throw Lang.makeThrow("Context '%s' had been deposed!", level);
	}

	public ObjectProxy fetch(String name) {
		checkBuffer();
		return objs.get(name);
	}

	public boolean save(String level, String name, ObjectProxy obj) {
		if (accept(level)) {
			checkBuffer();
			synchronized (this) {
				if (!objs.containsKey(name)) {
					return null != objs.put(name, obj);
				}
			}
		}
		return false;
	}

	protected boolean accept(String level) {
		return null != level && this.level.equals(level);
	}

	public boolean remove(String level, String name) {
		if (accept(level)) {
			checkBuffer();
			synchronized (this) {
				if (!objs.containsKey(name)) {
					return null != objs.remove(name);
				}
			}
		}
		return false;
	}

	public void clear() {
		checkBuffer();
		for (Entry<String, ObjectProxy> en : objs.entrySet()) {
			en.getValue().depose();
		}
		objs.clear();
	}

	public void depose() {
		clear();
		objs = null;
	}

}
