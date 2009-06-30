package org.nutz.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CmdParams {

	private Map<String, String> map;
	private ArrayList<String> names;

	public CmdParams() {
		this.map = new HashMap<String, String>();
		this.names = new ArrayList<String>();
	}

	public CmdParams add(String name, String value) {
		names.add(name);
		map.put(name, value);
		return this;
	}

	public CmdParams add(String name) {
		names.add(name);
		return this;
	}

	public ArrayList<String> names() {
		return names;
	}

	public String getName(int index) {
		try {
			return names.get(index);
		} catch (Exception e) {}
		return null;
	}

	public String getString(String... names) {
		for (String name : names)
			if (has(name))
				return map.get(name);
		return null;
	}

	public String one() {
		if (null == names || names.size() == 0)
			return "";
		return names.get(0);
	}

	public int getInt(String... names) {
		for (String name : names)
			if (has(name))
				return Integer.parseInt(map.get(name));
		return -1;
	}

	public boolean has(String... names) {
		for (String name : names)
			if (map.containsKey(name))
				return true;
		return false;
	}

}
