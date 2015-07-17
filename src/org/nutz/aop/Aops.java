package org.nutz.aop;

import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.ioc.aop.config.InterceptorPair;

/**
 * 方便生成Aop类
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class Aops {

    /**
     * aop指定的类
     * @param klass 需要aop化的原始类,不可以是抽象类或final类
     * @param ips 拦截器组
     * @param newName 新类的名称, 推荐格式    原始类名$用途
     * @return 改造完成的类
     */
    public static final Class<?> aop(Class<?> klass, List<InterceptorPair> ips, String newName) {
        if (ips == null || ips.isEmpty())
            return klass;
        if (newName == null)
            newName = klass.getName() + "$" + System.currentTimeMillis();
        ClassAgent agent = new AsmClassAgent();
        for (InterceptorPair interceptorPair : ips)
            agent.addInterceptor(interceptorPair.getMethodMatcher(),
                                 interceptorPair.getMethodInterceptor());
        return agent.define(DefaultClassDefiner.defaultOne(), klass); 
    }
    
    /**
     * 
     * @param klass 需要aop化的原始类,不可以是抽象类或final类
     * @param interceptor 拦截器
     * @param matcher 匹配需要拦截的方法, 可以通过MethodMatcherFactory的静态方法得到
     * @param newName 新类的名称, 推荐格式    原始类名$用途
     * @return 改造完成的类
     */
    public static final Class<?> aop(Class<?> klass, MethodInterceptor interceptor, MethodMatcher matcher, String newName) {
        if (matcher == null || interceptor == null)
            return klass;
        List<InterceptorPair> ips = new ArrayList<InterceptorPair>(1);
        ips.add(new InterceptorPair(interceptor, matcher));
        return aop(klass, ips, newName);
    }
}
