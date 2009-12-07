package org.nutz.aop.javassist;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.MethodInterceptor;

class JavassistMethodInterceptor implements MethodInterceptor {

	private List<MethodInterceptor> listeners;

	public JavassistMethodInterceptor(List<MethodInterceptor> listeners) {
		this.listeners = listeners;
	}

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		Object re = returnObj;
		for (MethodInterceptor ml : listeners)
			re = ml.afterInvoke(obj, re, method, args);
		return re;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		boolean re = true;
		for (MethodInterceptor ml : listeners)
			re &= ml.beforeInvoke(obj, method, args);
		return re;
	}

	public boolean whenError(Throwable e, Object obj, Method method, Object... args) {
		boolean re = true;
		for (MethodInterceptor ml : listeners)
			re &= ml.whenError(e, obj, method, args);
		return re;
	}

	public boolean whenException(Exception e, Object obj, Method method, Object... args) {
		boolean re = true;
		for (MethodInterceptor ml : listeners)
			re &= ml.whenException(e, obj, method, args);
		return re;
	}

}
