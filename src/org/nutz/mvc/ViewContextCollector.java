package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.util.Context;

/**
 * 视图上下文收集器
 * @param
 */
public interface ViewContextCollector {
    /**
     * 收集上下依赖的信息
     * @param req
     * @param obj
     * @return
     */
    public Context collect(HttpServletRequest req, Object obj);
}
