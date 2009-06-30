package org.nutz.aop.javassist;

import java.lang.reflect.Method;
import java.util.List;

import org.nutz.aop.MethodListener;

public class JavassistMethodListener implements MethodListener {

	private List<MethodListener> listeners;

	public JavassistMethodListener(List<MethodListener> listeners) {
		this.listeners = listeners;
	}

	@Override
	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		Object re = returnObj;
		for (MethodListener ml : listeners)
			re = ml.afterInvoke(obj, re, method, args);
		return re;
	}

	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		boolean re = true;
		for (MethodListener ml : listeners)
			re = ml.beforeInvoke(obj, method, args);
		return re;
	}

	@Override
	public void whenError(Throwable e, Object obj, Method method, Object... args) {
		for (MethodListener ml : listeners)
			ml.whenError(e, obj, method, args);
	}

	@Override
	public void whenException(Exception e, Object obj, Method method, Object... args) {
		for (MethodListener ml : listeners)
			ml.whenException(e, obj, method, args);
	}

}
