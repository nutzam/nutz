package org.nutz.ioc.aop.impl;

import java.util.List;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.aop.config.impl.AnnotationAopConfigration;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 通过AopConfigration来识别需要拦截的方法,并根据需要生成新的类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class DefaultMirrorFactory implements MirrorFactory {
    
    private static final Log log = Logs.get();

    private Ioc ioc;

    private ClassDefiner cd;

    private AopConfigration aopConfigration;

    public DefaultMirrorFactory(Ioc ioc) {
        this.ioc = ioc;
        this.cd = new DefaultClassDefiner(getClass().getClassLoader());
    }

    @SuppressWarnings("unchecked")
    public <T> Mirror<T> getMirror(Class<T> type, String name) {
        if (MethodInterceptor.class.isAssignableFrom(type)
            || type.getName().endsWith(ClassAgent.CLASSNAME_SUFFIX)
            || AopConfigration.IOCNAME.equals(name)
            || AopConfigration.class.isAssignableFrom(type)) {
            return Mirror.me(type);
        }
        try {
            return (Mirror<T>) Mirror.me(cd.load(type.getName() + ClassAgent.CLASSNAME_SUFFIX));
        }
        catch (ClassNotFoundException e) {}
        if (aopConfigration == null)
            if (ioc.has(AopConfigration.IOCNAME))
                aopConfigration = ioc.get(AopConfigration.class, AopConfigration.IOCNAME);
            else
                aopConfigration = new AnnotationAopConfigration();
        List<InterceptorPair> interceptorPairs = aopConfigration.getInterceptorPairList(ioc, type);
        if (interceptorPairs == null || interceptorPairs.size() < 1) {
            if (log.isDebugEnabled())
                log.debugf("%s , no config to enable AOP.", type);
            return Mirror.me(type);
        }
        ClassAgent agent = new AsmClassAgent();
        for (InterceptorPair interceptorPair : interceptorPairs)
            agent.addInterceptor(    interceptorPair.getMethodMatcher(),
                                    interceptorPair.getMethodInterceptor());
        return Mirror.me(agent.define(cd, type));
    }

    public void setAopConfigration(AopConfigration aopConfigration) {
        this.aopConfigration = aopConfigration;
    }
}
