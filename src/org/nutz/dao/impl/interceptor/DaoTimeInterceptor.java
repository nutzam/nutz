package org.nutz.dao.impl.interceptor;

import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 打印执行耗时. 默认不启用.
 * 
 * @author wendal
 * @since 1.r.58
 */
public class DaoTimeInterceptor implements DaoInterceptor {

    private static final Log log = Logs.get();

    public void filter(DaoInterceptorChain chain) throws DaoException {
        Stopwatch sw = Stopwatch.begin();
        try {
            chain.doChain();
        }
        finally {
            sw.stop();
            if (log.isDebugEnabled())
                log.debugf("time=%sms, sql=%s",
                           sw.getDuration(),
                           chain.getDaoStatement().toString());
        }
    }

}
