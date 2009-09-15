package org.nutz.dao.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class VarSet {

	private HashMap<String, Object> map;
	private List<String> keys;

	VarSet() {
		this.map = new HashMap<String, Object>();
		this.keys = new ArrayList<String>();
	}

	VarSet set(String name, Object value) {
		if (!map.containsKey(name))
			keys.add(name);
		map.put(name, value);
		return this;
	}

	Object get(String name) {
		return map.get(name);
	}

	List<String> keys() {
		return keys;
	}

}
