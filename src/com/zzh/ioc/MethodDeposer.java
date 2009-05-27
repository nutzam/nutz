package com.zzh.ioc;

import java.lang.reflect.Method;

import com.zzh.lang.Lang;

public class MethodDeposer implements Deposer<Object> {

	private Method method;

	public MethodDeposer(Method method) {
		this.method = method;
	}

	@Override
	public void depose(Object obj) {
		try {
			method.invoke(obj);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
