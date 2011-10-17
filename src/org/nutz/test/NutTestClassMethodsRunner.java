package org.nutz.test;

import java.lang.reflect.Method;

import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.runner.notification.RunNotifier;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 为Nutz定制的测试方法运行器
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class NutTestClassMethodsRunner extends TestClassMethodsRunner {

	private static final Log log = Logs.get();

	protected Class<?> klass;

	public NutTestClassMethodsRunner(Class<?> klass) {
		super(klass);
		NutTestContext.me().mirror = Mirror.me(klass);
		NutTestContext.me().ioc = null;
		this.klass = klass;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void invokeTestMethod(final Method method, final RunNotifier notifier) {
		// 处理事务回滚问题
		NutTest nutTest = method.getAnnotation(NutTest.class);
		if (nutTest == null)
			nutTest = (NutTest) NutTestContext.me().mirror.getAnnotation(NutTest.class);
		final boolean needRollback = nutTest != null && nutTest.rollback();
		
		try {
			// 检查Ioc支持
			IocBy iocBy = (IocBy) NutTestContext.me().mirror.getAnnotation(IocBy.class);
			if (iocBy != null)
				NutTestContext.me().ioc = Mirror.me(iocBy.type()).born().create(null, iocBy.args());
			else
				NutTestContext.me().ioc = null;

			// 打印调试信息
			if (log.isDebugEnabled()) {
				log.debug("->" + method + " -> auto-rollback=" + needRollback);
				if (NutTestContext.me().ioc == null)
					log.debug("@IocBy not found ,run without Ioc support !!");
				else
					log.debug("@IocBy found ,run with Ioc support ^_^");
			}
			
			if (needRollback)
				try {
					Trans.exec(new Atom() {
						@Override
						public void run() {
							NutTestClassMethodsRunner.super.invokeTestMethod(method, notifier);
							throw JustRollback.me();// 这样,无论原方法是否跑异常,事务模板都能收到异常,并回滚
						}
					});
				}
				catch (JustRollback e) {}
			else
				// 按传统方法执行,无需通过事务模板
				super.invokeTestMethod(method, notifier);
		}
		finally {
			//确保Ioc容器被关闭
			if (NutTestContext.me().ioc != null) {
				try {
					NutTestContext.me().ioc.depose();
				} finally {
					NutTestContext.me().ioc = null;
				}
			}
		}
	}

	@Override
	protected Object createTest() throws Exception {
		if (NutTestContext.me().ioc != null && klass.getAnnotation(IocBean.class) != null)
			return NutTestContext.me().ioc.get(klass);
		return super.createTest();
	}
}
