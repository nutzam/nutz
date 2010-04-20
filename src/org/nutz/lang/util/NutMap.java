package org.nutz.lang.util;

import java.util.HashMap;

import org.nutz.castor.Castors;

@SuppressWarnings("serial")
public class NutMap extends HashMap<String, Object> {

	public int getInt(String key) {
		return getInt(key, -1);
	}

	public int getInt(String key, int dft) {
		Object obj = get(key);
		if (null == obj)
			return dft;
		return Castors.me().castTo(obj, int.class);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String dft) {
		Object obj = get(key);
		if (null == obj)
			return dft;
		return Castors.me().castTo(obj, String.class);
	}

}
