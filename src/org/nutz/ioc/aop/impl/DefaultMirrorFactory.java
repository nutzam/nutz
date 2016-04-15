package org.nutz.ioc.aop.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.nutz.ioc.aop.config.impl.ComboAopConfigration;
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

    private List<AopConfigration> list;

    private static final Object lock = new Object();

    public DefaultMirrorFactory(Ioc ioc) {
        this.ioc = ioc;
    }

    public <T> Mirror<T> getMirror(Class<T> type, String name) {
        if (MethodInterceptor.class.isAssignableFrom(type)
            || type.getName().endsWith(ClassAgent.CLASSNAME_SUFFIX)
            || (name != null && name.startsWith(AopConfigration.IOCNAME))
            || AopConfigration.class.isAssignableFrom(type)
            || Modifier.isAbstract(type.getModifiers())) {
            return Mirror.me(type);
        }

        if (list == null) {
            List<AopConfigration> tmp = new ArrayList<AopConfigration>();
            boolean flag = true;
            String[] names = ioc.getNames();
            Arrays.sort(names);
            for (String beanName : names) {
                if (!beanName.startsWith(AopConfigration.IOCNAME))
                    continue;
                AopConfigration cnf = ioc.get(AopConfigration.class, beanName);
                tmp.add(cnf);
                if (cnf instanceof AnnotationAopConfigration)
                    flag = false;
                if (cnf instanceof ComboAopConfigration) {
                    if (((ComboAopConfigration) cnf).hasAnnotationAop()) {
                        flag = false;
                    }
                }
            }
            if (flag)
                tmp.add(new AnnotationAopConfigration());
            list = tmp;
        }
        List<InterceptorPair> interceptorPairs = new ArrayList<InterceptorPair>();
        for (AopConfigration cnf : list) {
            List<InterceptorPair> tmp = cnf.getInterceptorPairList(ioc, type);
            if (tmp != null && tmp.size() > 0)
                interceptorPairs.addAll(tmp);
        }
        if (interceptorPairs.isEmpty()) {
            if (log.isDebugEnabled())
                log.debugf("Load %s without AOP", type);
            return Mirror.me(type);
        }

        int mod = type.getModifiers();
        if (Modifier.isFinal(mod) || Modifier.isAbstract(mod)) {
            log.infof("[%s] configure to use AOP, but it is final/abstract, skip it", type.getName());
            return Mirror.me(type);
        }

        synchronized (lock) {
            if (cd == null) {
                cd = DefaultClassDefiner.defaultOne();
            }
            ClassAgent agent = new AsmClassAgent();
            for (InterceptorPair interceptorPair : interceptorPairs)
                agent.addInterceptor(interceptorPair.getMethodMatcher(),
                                     interceptorPair.getMethodInterceptor());
            return Mirror.me(agent.define(cd, type));
        }

    }

    public void setAopConfigration(AopConfigration aopConfigration) {
        this.list = new ArrayList<AopConfigration>();
        this.list.add(aopConfigration);
    }
}
