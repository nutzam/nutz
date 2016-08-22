package org.nutz.dao;

/**
 * Dao操作拦截器
 * @author wendal
 * @see org.nutz.dao.impl.interceptor.DaoLogInterceptor
 * @see org.nutz.dao.impl.interceptor.DaoTimeInterceptor
 */
public interface DaoInterceptor {

    void filter(DaoInterceptorChain chain) throws DaoException;
}
