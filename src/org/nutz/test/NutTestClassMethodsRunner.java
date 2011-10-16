package org.nutz.test;

import java.lang.reflect.Method;

import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.runner.notification.RunNotifier;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class NutTestClassMethodsRunner extends TestClassMethodsRunner {
	
	private Class<?> klass;
	
	public NutTestClassMethodsRunner(Class<?> klass) {
		super(klass);
		NutTestContext.me().mirror = Mirror.me(klass);
		this.klass = klass;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void invokeTestMethod(final Method method, final RunNotifier notifier) {
		NutTest nutTest = method.getAnnotation(NutTest.class);
		if (nutTest == null)
			nutTest = (NutTest) NutTestContext.me().mirror.getAnnotation(NutTest.class);
		final boolean needRollback = nutTest != null && nutTest.rollback();

		IocBy iocBy = (IocBy) NutTestContext.me().mirror.getAnnotation(IocBy.class);
		if (iocBy != null)
			NutTestContext.me().ioc = Mirror.me(iocBy.type()).born().create(null, iocBy.args());
		else
			NutTestContext.me().ioc = null;
		
		if (needRollback)
			try {
				Trans.exec(new Atom(){
					@Override
					public void run() {
						NutTestClassMethodsRunner.super.invokeTestMethod(method, notifier);
						throw JustRollback.me();
					}
				});
			} catch (JustRollback e) {}
		else
			super.invokeTestMethod(method, notifier);
	}
	
	@Override
	protected Object createTest() throws Exception {
		if (NutTestContext.me().ioc != null && klass.getAnnotation(IocBean.class) != null)
			return NutTestContext.me().ioc.get(klass);
		return super.createTest();
	}
}
