package org.nutz.aop.asm.test;

import java.lang.reflect.Method;

import org.nutz.aop.interceptor.AbstractMethodInterceptor;
import org.nutz.castor.Castors;

public class MyMethodInterceptor extends AbstractMethodInterceptor {

    public Object afterInvoke(Object obj, Object result, Method arg2, Object... objs) {
        System.out.println("After..... " + arg2.getName());
        printArgs(objs);
        return result;
    }

    public boolean beforeInvoke(Object arg0, Method arg1, Object... objs) {
        System.out.println("-----------------------------------------------------");
        System.out.println("Before.... " + arg1.getName());
        printArgs(objs);
        return true;
    }

    public boolean whenError(Throwable e, Object arg1, Method arg2, Object... objs) {
        System.out.println("抛出了Throwable " + e);
        printArgs(objs);
        return true;
    }

    public boolean whenException(Exception e, Object arg1, Method arg2, Object... objs) {
        System.out.println("抛出了Exception " + e);
        printArgs(objs);
        return true;
    }

    void printArgs(Object... objs) {
        System.out.println("参数 " + Castors.me().castToString(objs));
    }

}
