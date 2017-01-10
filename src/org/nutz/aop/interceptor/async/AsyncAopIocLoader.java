package org.nutz.aop.interceptor.async;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;

public class AsyncAopIocLoader extends SimpleAopMaker<Async>{
	
	protected ExecutorService es;
	
	public AsyncAopIocLoader(){
		this(32);
	}
	
	public AsyncAopIocLoader(int size) {
		this(Executors.newFixedThreadPool(size));
	}
	
	protected AsyncAopIocLoader(ExecutorService es) {
	    this.es = es;
	}
	
	public List<? extends MethodInterceptor> makeIt(Async async, Method method, Ioc ioc) {
		if (!async.enable())
			return null;
		return Arrays.asList(new AsyncMethodInterceptor(method, async, es));
	}
	
	public void depose() throws Exception {
		if (es != null)
			es.shutdownNow();
	}
	
	public ExecutorService getExecutorService() {
        return es;
    }
	
	public void setExecutorService(ExecutorService es) {
        this.es = es;
    }
}
