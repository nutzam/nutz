package org.nutz.dao.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class SimpleVarSet implements VarSet {

	private HashMap<String, Object> map;

	SimpleVarSet() {
		this.map = new HashMap<String, Object>();
	}

	public VarSet set(String name, Object value) {
		map.put(name, value);
		return this;
	}

	public Object get(String name) {
		return map.get(name);
	}

	public Set<String> keys() {
		return map.keySet();
	}

	public VarSet putAll(Map<String, Object> map) {
		this.map.putAll(map);
		return this;
	}

}
