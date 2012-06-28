package org.nutz.ioc.aop.config;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.MethodMatcher;

/**
 * 
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class InterceptorPair {

    private MethodInterceptor methodInterceptor;

    private MethodMatcher methodMatcher;

    public InterceptorPair(MethodInterceptor methodInterceptor, MethodMatcher methodMatcher) {
        super();
        this.methodInterceptor = methodInterceptor;
        this.methodMatcher = methodMatcher;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

}
