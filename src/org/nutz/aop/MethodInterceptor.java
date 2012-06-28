package org.nutz.aop;

/**
 * 方法拦截器v2
 * <p>
 * 你可以通过实现接口加入自己的额外逻辑
 * 
 * @author wendal(wendal1985@gmail.com)
 * @see org.nutz.aop.InterceptorChain
 */
public interface MethodInterceptor {

    void filter(InterceptorChain chain) throws Throwable;

}
