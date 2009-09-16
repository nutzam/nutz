package org.nutz.dao.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class SimpleVarSet implements VarSet {
	private HashMap<String, Object> map;
	private List<String> keys;

	SimpleVarSet() {
		this.map = new HashMap<String, Object>();
		this.keys = new ArrayList<String>();
	}

	public VarSet set(String name, Object value) {
		if (!map.containsKey(name))
			keys.add(name);
		map.put(name, value);
		return this;
	}

	public Object get(String name) {
		return map.get(name);
	}

	public List<String> keys() {
		return keys;
	}
}
