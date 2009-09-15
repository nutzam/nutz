package org.nutz.dao.sql;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.util.LinkedIntArray;

class VarIndexes {

	VarIndexes() {
		map = new HashMap<String, LinkedIntArray>();
	}

	private Map<String, LinkedIntArray> map;

	void add(String name, int index) {
		LinkedIntArray lia = map.get(name);
		if (null == lia) {
			lia = new LinkedIntArray();
			map.put(name, lia);
		}
		lia.push(index);
	}

	int[] get(String name) {
		LinkedIntArray lia = map.get(name);
		if (null == lia)
			return null;
		return lia.toArray();
	}

}
