package com.zzh.ioc.maker;

import java.lang.reflect.Method;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class JavaObjectMaker implements ValueMaker {

	@Override
	public String forType() {
		return Val.java;
	}

	@Override
	public Object make(Val val) {
		String callPath = val.getValue().toString();
		int pos = callPath.lastIndexOf('.');
		String className = Strings.trim(callPath.substring(0, pos));
		String fieldName = Strings.trim(callPath.substring(pos + 1));
		try {
			Mirror<?> mirror = Mirror.me(Class.forName(className));
			Method method = null;
			try {
				method = mirror.getGetter(fieldName);
			} catch (Exception e) {
				try {
					method = mirror.getType().getMethod(fieldName);
				} catch (Exception e1) {}
			}
			if (null != method)
				return method.invoke(null);
			return mirror.getField(fieldName).get(null);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
