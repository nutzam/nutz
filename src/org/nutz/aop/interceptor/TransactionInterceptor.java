package org.nutz.aop.interceptor;

import java.lang.reflect.Method;
import java.sql.Connection;

import org.nutz.aop.AbstractMethodInterceptor;
import org.nutz.lang.Lang;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 可以插入事务的拦截器
 * <p/> 默认事务登记为 Connection.TRANSACTION_READ_COMMITTED
 * <p/> 可以在构建拦截器时设置
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class TransactionInterceptor extends AbstractMethodInterceptor {

	private int level;

	public TransactionInterceptor() {
		this.level = Connection.TRANSACTION_READ_COMMITTED;
	}

	public TransactionInterceptor(int level) {
		this.level = level;
	}

	@Override
	public boolean beforeInvoke(Object obj, Method method, Object... args) {
		return false;
	}

	@Override
	public Object afterInvoke(	final Object obj,
								Object returnObj,
								final Method method,
								final Object... args) {
		final Object[] returnValue = new Object[1];
		Trans.exec(level, new Atom() {
			public void run() {
				try {
					returnValue[0] = method.invoke(obj, args);
				}
				catch (Throwable e) {
					throw Lang.wrapThrow(e);
				}
			}
		});
		return returnValue[0];
	}
}
