package org.nutz.dao.sql;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public Set<String> keys() {
		Set<String> keys = new LinkedHashSet<String>();
		for (VarSet vs : vss)
			keys.addAll(vs.keys());
		return keys;
	}

	public VarSet set(String name, Object value) {
		for (VarSet vs : vss)
			vs.set(name, value);
		return this;
	}

	public VarSet putAll(Map<String, Object> map) {
		for (VarSet vs : vss)
			vs.putAll(map);
		return this;
	}

}
