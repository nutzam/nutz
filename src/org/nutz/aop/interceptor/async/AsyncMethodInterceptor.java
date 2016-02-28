package org.nutz.aop.interceptor.async;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;

public class AsyncMethodInterceptor implements MethodInterceptor {
	
	protected ExecutorService es;
	protected boolean hasFuture;
	
	public AsyncMethodInterceptor(Method method, Async async, ExecutorService es) {
		this.es = es;
		hasFuture = Future.class.isAssignableFrom(method.getReturnType());
	}

	public void filter(final InterceptorChain chain) throws Throwable {
		Future<Object> future = es.submit(new _async_task(chain, hasFuture));
		if (hasFuture) {
			chain.setReturnValue(future);
		}
	}

}

class _async_task implements Callable<Object> {
	InterceptorChain chain;
	boolean hasFuture;
	
	public _async_task(InterceptorChain chain, boolean hasFuture) {
		this.chain = chain;
		this.hasFuture = hasFuture;
	}

	@SuppressWarnings("unchecked")
	public Object call() throws Exception {
		try {
			Object re = chain.doChain().getReturn();
			if (hasFuture && re != null) {
				return ((Future<Object>)re).get();
			}
			return null;
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
	
}