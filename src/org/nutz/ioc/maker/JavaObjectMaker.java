package org.nutz.ioc.maker;

import java.lang.reflect.Method;

import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Val;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public class JavaObjectMaker implements ValueMaker {

	public String forType() {
		return Val.java;
	}

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
				} catch (Exception e1) {
				}
			}
			if (null != method)
				return method.invoke(null);
			return mirror.getField(fieldName).get(null);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
