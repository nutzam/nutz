package org.nutz.aop.asm;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.nutz.Nutzs;
import org.nutz.aop.AbstractMethodInterceptor;
import org.nutz.aop.RegexMethodMatcher;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.aop.asm.test.Aop1;
import org.nutz.lang.Mirror;

public class RegexMethodMatcherTest {

	@Test
	public void testRegexMethodMatcherStringStringInt() throws Throwable {
		AsmClassAgent agent = new AsmClassAgent();
		MyL interceptor = new MyL();
		agent.addListener(new RegexMethodMatcher(null, "nonArgsVoid", 0), interceptor);
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