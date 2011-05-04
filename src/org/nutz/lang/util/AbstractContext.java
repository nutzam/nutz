package org.nutz.lang.util;

import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;

//TODO XXX 这是临时伪造的,等待zozoh上传真实版本
public abstract class AbstractContext implements Context {
	
	public <T> T getAs(Class<T> type, String name) {
		return Castors.me().castTo(get(name), type);
	}

	public int getInt(String name) {
		return getAs(int.class, name);
	}

	public String getString(String name) {
		return getAs(String.class, name);
	}

	public boolean getBoolean(String name) {
		return getAs(boolean.class, name);
	}

	public float getFloat(String name) {
		return getAs(float.class, name);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMap(String name) {
		return getAs(Map.class, name);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getList(String name) {
		return getAs(List.class, name);
	}

	public Context clone() {
		SimpleContext context = new SimpleContext();
		for (String key : keys()) {
			context.set(key, get(key));
		}
		return context;
	}
	
	public Map<String, Object> getInnerMap() {
		return null;
	}
	
	@Override
	public Context putAll(Object obj) {
		return this;
	}
}
