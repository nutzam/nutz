package org.nutz.ioc.aop.config.impl.simple;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.interceptor.LoggingMethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="$aop_def")
public class DefSimpleAop extends SimpleAopMaker<Def> {
    
    @Inject
    public OneObject oneObject; 

    public List<? extends MethodInterceptor> makeIt(Def t, Method method, Ioc ioc) {
        return Arrays.asList(new LoggingMethodInterceptor());
    }

    public String[] getName() {
        return new String[0];
    }
    
    public boolean has(String name) {
        return false;
    }
}
