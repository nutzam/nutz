package org.nutz.ioc.loader.map;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;

/**
 * 从一个 Map 对象中读取配置信息，支持 Parent
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MapLoader implements IocLoader {

	private Map<String, Map<String, Object>> map;

	protected MapLoader() {
		map = new HashMap<String, Map<String, Object>>();
	}

	public MapLoader(Map<String, Map<String, Object>> map) {
		this.map = map;
	}

	public Map<String, Map<String, Object>> getMap() {
		return map;
	}

	public void setMap(Map<String, Map<String, Object>> map) {
		this.map = map;
	}

	public String[] getName() {
		return map.keySet().toArray(new String[map.size()]);
	}

	public boolean has(String name) {
		return map.containsKey(name);
	}

	public IocObject load(String name) throws ObjectLoadException {
		return Iocs.map2iobj(getMap(name));
	}

	/**
	 * Inner Object can not support 'parent'.
	 * 
	 * @param name
	 *            object Map name
	 * @return object Map
	 */
	private Map<String, Object> getMap(String name) {
		Map<String, Object> m = map.get(name);
		String pKey = (String) m.get("parent");
		// If link to parent
		if (!Strings.isBlank(pKey)) {
			// Get the parent
			Map<String, Object> parent = getMap(pKey);
			// create new Map
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.putAll(parent);
			// merge with current map
			newMap.putAll(m);
			return newMap;
		}
		return m;
	}

}
