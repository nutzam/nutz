package org.nutz.mvc.impl.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 使用容器原生的Session实现 == 等于什么都没做.
 *
 */
public class NopSessionProvider extends AbstractSessionProvider {

    @Override
    public HttpSession createSession(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     ServletContext servletContext) {
        // 使用容器原生的Session实现 == 等于什么都没做
        return req.getSession(true);
    }

    @Override
    public HttpSession getExistSession(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext) {
        return req.getSession(false);
    }
}
