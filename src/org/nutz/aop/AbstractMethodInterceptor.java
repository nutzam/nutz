package org.nutz.aop;

import java.lang.reflect.Method;

public class AbstractMethodInterceptor implements MethodInterceptor {

	public void filter(InterceptorChain chain) throws Throwable{
		try {
			if (beforeInvoke(chain.getCallingObj(), chain.getCallingMethod(), chain.getArgs()))
				chain.doChain();
			Object obj = afterInvoke(chain.getCallingObj(), chain.getReturn(), chain.getCallingMethod(), chain.getArgs());
			chain.setReturnValue(obj);
		}
		catch (Exception e) {
			if (whenException(e, chain.getCallingObj(), chain.getCallingMethod(), chain.getArgs()))
				throw e;
		}
		catch (Throwable e) {
			if (whenError(e, chain.getCallingObj(), chain.getCallingMethod(), chain.getArgs()))
				throw e;
		}

	}

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
