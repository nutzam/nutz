package org.nutz.aop;

import java.lang.reflect.Method;

/**
 * 方法拦截器的空实现
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class AbstractMethodInterceptor implements MethodInterceptor {

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		return obj;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		return true;
	}

	public void whenError(Throwable e, Object obj, Method method, Object... args) {
	}

	public void whenException(Exception e, Object obj, Method method, Object... args) {
	}

}
