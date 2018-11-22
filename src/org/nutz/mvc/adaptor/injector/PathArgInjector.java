package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.ParamInjector;

public class PathArgInjector implements ParamInjector {

    protected Class<?> type;

    public PathArgInjector(Class<?> type) {
        this.type = type;
    }

    /**
     * @param req
     *            请求对象
     * @param resp
     *            响应对象
     * @param refer
     *            这个参考字段，如果有值，表示是路径参数的值，那么它比 request 里的参数优先
     * @return 注入对象
     */
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        if (null == refer)
            return null;
        return Castors.me().castTo(refer, type);
    }

}
