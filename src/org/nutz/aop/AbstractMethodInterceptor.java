package org.nutz.aop;

import java.lang.reflect.Method;

/**
 * 方法拦截器的空实现
 * <p>本实现不会改变被拦截方法的行为.
 * @author wendal(wendal1985@gmail.com)
 * @see org.nutz.aop.MethodInterceptor
 */
public abstract class AbstractMethodInterceptor implements MethodInterceptor {

	public Object afterInvoke(Object obj, Object returnObj, Method method, Object... args) {
		return returnObj;
	}

	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		return true;
	}

	public boolean whenError(Throwable e, Object obj, Method method, Object... args) {
		return true;
	}

	public boolean whenException(Exception e, Object obj, Method method, Object... args) {
		return true;
	}

}
