package org.nutz.ioc.aop.config.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.MethodMatcher;
import org.nutz.aop.matcher.SimpleMethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.lang.Mirror;

/**
 * 通过扫描@Aop标注过的Method判断需要拦截哪些方法
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class AnnotationAopConfigration implements AopConfigration {

    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> clazz) {
        Mirror<?> mirror = Mirror.me(clazz);
        List<Method> aops = this.getAopMethod(mirror);
        List<InterceptorPair> ipList = new ArrayList<InterceptorPair>();
        if (aops.size() < 1)
            return ipList;
        for (Method m : aops) {
            MethodMatcher mm = new SimpleMethodMatcher(m);
            for (String nm : m.getAnnotation(Aop.class).value())
                ipList.add(new InterceptorPair(ioc.get(MethodInterceptor.class, nm), mm));
        }
        return ipList;
    }

    private <T> List<Method> getAopMethod(Mirror<T> mirror) {
        List<Method> aops = new LinkedList<Method>();
        for (Method m : mirror.getMethods())
            if (null != m.getAnnotation(Aop.class)) {
                int modify = m.getModifiers();
                if (!Modifier.isAbstract(modify))
                    if (!Modifier.isFinal(modify))
                        if (!Modifier.isPrivate(modify))
                            if (!Modifier.isStatic(modify))
                                aops.add(m);
            }
        return aops;
    }
}
