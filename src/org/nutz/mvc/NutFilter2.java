package org.nutz.mvc;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 为了兼容老的NutFilter,把逻辑独立出来, 仅用于过滤Jsp请求之类的老特性
 *
 */
public class NutFilter2 implements Filter {

    private String selfName;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        if (selfName == null) {
            selfName = Mvcs.ctx().nutConfigs.keySet().iterator().next();
            if (selfName == null) {
                chain.doFilter(req, resp);
                return;
            }
        }
        boolean needReset = false;
        if (Mvcs.getName() == null) {
            HttpServletRequest req2 = (HttpServletRequest) req;
            HttpServletResponse resp2 = (HttpServletResponse) resp;
            Mvcs.set(selfName, req2, resp2);
            Mvcs.updateRequestAttributes(req2);
            needReset = true;
        }
        try {
            chain.doFilter(req, resp);
        }
        finally {
            if (needReset) {
                Mvcs.resetALL();
            }
        }
    }

    @Override
    public void init(FilterConfig conf) throws ServletException {}

    @Override
    public void destroy() {}

}
