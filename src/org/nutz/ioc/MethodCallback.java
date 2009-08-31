package org.nutz.ioc;

import java.lang.reflect.Method;

import org.nutz.lang.Lang;

public class MethodCallback implements ObjCallback<Object> {

	private Method method;

	public MethodCallback(Method method) {
		this.method = method;
	}

	public void invoke(Object obj) {
		try {
			method.invoke(obj);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
