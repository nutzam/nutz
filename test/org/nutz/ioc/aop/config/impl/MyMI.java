package org.nutz.ioc.aop.config.impl;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

public class MyMI implements MethodInterceptor {
    
    private int time;

    public void filter(InterceptorChain chain) throws Throwable {
        time++;
        chain.doChain();
    }

    public int getTime() {
        return time;
    }
}
