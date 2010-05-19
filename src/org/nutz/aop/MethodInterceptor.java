package org.nutz.aop;


/**
 * 方法拦截器v2
 * <p>
 * 你可以�?过实现接�?加入自己的额外�?�?
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public interface MethodInterceptor {

	void filter(InterceptorChain chain);

}
