package org.nutz.test;

import java.lang.reflect.Method;

import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.runner.notification.RunNotifier;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class NutTestClassMethodsRunner extends TestClassMethodsRunner {
	
	public NutTestClassMethodsRunner(Class<?> klass) {
		super(klass);
	}

	@Override
	protected void invokeTestMethod(final Method method, final RunNotifier notifier) {
		NutTest nutTest = method.getAnnotation(NutTest.class);
		boolean needRollback = nutTest != null && nutTest.rollback();
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
}
