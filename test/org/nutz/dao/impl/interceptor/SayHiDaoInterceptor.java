package org.nutz.dao.impl.interceptor;

import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SayHiDaoInterceptor implements DaoInterceptor {
    
    private static final Log log = Logs.get();

    @Override
    public void filter(DaoInterceptorChain chain) throws DaoException {
        log.debug("nop ...");
        chain.doChain();
    }

}
