package org.nutz.aop.interceptor;

import java.sql.Connection;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * 可以插入事务的拦截器
 * <p/>
 * 默认事务登记为 Connection.TRANSACTION_READ_COMMITTED
 * <p/>
 * 可以在构建拦截器时设置
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class TransactionInterceptor implements MethodInterceptor {

    private int level;

    public TransactionInterceptor() {
        this.level = Connection.TRANSACTION_READ_COMMITTED;
    }

    public TransactionInterceptor(int level) {
        this.level = level;
    }

    public void filter(final InterceptorChain chain) {
        Trans.exec(level, new Atom() {
            public void run() {
                try {
                    chain.doChain();
                }
                catch (Throwable e) {
                    throw Lang.wrapThrow(e);
                }
            }
        });
    }

}
