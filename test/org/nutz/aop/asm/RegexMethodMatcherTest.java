package org.nutz.aop.asm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;
import org.nutz.Nutzs;
import org.nutz.aop.asm.test.Aop1;
import org.nutz.aop.interceptor.AbstractMethodInterceptor;
import org.nutz.aop.interceptor.LoggingMethodInterceptor;
import org.nutz.aop.matcher.MethodMatcherFactory;
import org.nutz.aop.matcher.RegexMethodMatcher;
import org.nutz.lang.Mirror;

public class RegexMethodMatcherTest {

    @Test
    public void testRegexMethodMatcherStringStringInt() throws Throwable {
        AsmClassAgent agent = new AsmClassAgent();
        MyL interceptor = new MyL();
        agent.addInterceptor(new RegexMethodMatcher(null, "nonArgsVoid", 0), interceptor);
        agent.addInterceptor(MethodMatcherFactory.matcher(".*"), new LoggingMethodInterceptor());
        Mirror<Aop1> mirror = Mirror.me(agent.define(Nutzs.cd(), Aop1.class));
        Aop1 aop1 = mirror.born("Nutz");
        aop1.nonArgsVoid();
        assertFalse(interceptor.runned);
        aop1.argsVoid(null);
        assertTrue(interceptor.runned);
    }

}

class MyL extends AbstractMethodInterceptor {

    public boolean runned = false;

    @Override
    public boolean beforeInvoke(Object obj, Method method, Object... args) {
        runned = true;
        return super.beforeInvoke(obj, method, args);
    }
}