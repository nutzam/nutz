package org.nutz.aop;

import java.lang.reflect.Method;

public interface MethodListener {

	/**
	 * @param obj
	 * @param method
	 * @param args
	 *            argument of the method
	 * @return true, it will call super method. false, it will not call super
	 *         method.
	 */
	boolean beforeInvoke(Object obj, Method method, Object... args);

	void whenException(Exception e, Object obj, Method method, Object... args);

	void whenError(Throwable e, Object obj, Method method, Object... args);

	/**
	 * @param obj
	 * @param returnObj
	 *            the result return by super
	 * @param method
	 * @param args
	 * @return The object will be return to client.
	 */
	Object afterInvoke(Object obj, Object returnObj, Method method, Object... args);

}
