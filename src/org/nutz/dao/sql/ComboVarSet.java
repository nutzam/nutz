package org.nutz.dao.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ComboVarSet implements VarSet {

	public ComboVarSet() {
		vss = new LinkedList<VarSet>();
	}

	private List<VarSet> vss;

	ComboVarSet add(VarSet vs) {
		vss.add(vs);
		return this;
	}

	ComboVarSet clear() {
		vss.clear();
		return this;
	}

	public Object get(String name) {
		List<Object> list = new ArrayList<Object>(vss.size());
		for (VarSet vs : vss)
			list.add(vs.get(name));
		return list;
	}

	public List<String> keys() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (VarSet vs : vss)
			for (String key : vs.keys())
				map.put(key, null);
		List<String> keys = new ArrayList<String>(map.size());
		keys.addAll(map.keySet());
		return keys;
	}

	public VarSet set(String name, Object value) {
		for (VarSet vs : vss)
			vs.set(name, value);
		return this;
	}

}
