package com.zzh.ioc;

import java.util.Map;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class JavaObjectMaker extends ObjectMaker<Object> {

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("java");
	}

	@Override
	protected Object make(Map<String, Object> properties) {
		String callPath = properties.get("java").toString();
		int pos = callPath.lastIndexOf('.');
		String className = Strings.trim(callPath.substring(0, pos));
		String callName = Strings.trim(callPath.substring(pos + 1));
		try {
			return Mirror.me(Class.forName(className)).getValue(null, callName);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
