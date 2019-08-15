package org.nutz.mvc.impl.contextCollector;

import org.nutz.lang.util.Context;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewContextCollector;

import javax.servlet.http.HttpServletRequest;

/**
 * 复制全局的上下文对象
 */
public class ServletContextCollector implements ViewContextCollector {
    @Override
    public Context collect(HttpServletRequest req, Object obj) {
        return (Context) Mvcs.getServletContext().getAttribute(Loading.CONTEXT_NAME);
    }
}
