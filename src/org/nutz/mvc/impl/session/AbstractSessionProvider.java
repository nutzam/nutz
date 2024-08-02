package org.nutz.mvc.impl.session;

import org.nutz.mvc.SessionProvider;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 抽象的SessionProvider实现,可以作为
 * 
 * @author wendal
 * 
 */
public abstract class AbstractSessionProvider implements SessionProvider {

    private static final Object lock = new Object();

    public HttpServletRequest filter(final HttpServletRequest req,
                                     final HttpServletResponse resp,
                                     final ServletContext servletContext) {
        return new SessionProviderHttpServletRequestWrapper(req, resp, servletContext);
    }

    /**
     * 子类覆盖此方法,以创建一个新的Session对象,或者任何你想做的事
     */
    public abstract HttpSession createSession(final HttpServletRequest req,
                                              final HttpServletResponse resp,
                                              final ServletContext servletContext);

    public abstract HttpSession getExistSession(final HttpServletRequest req,
                                                final HttpServletResponse resp,
                                                final ServletContext servletContext);

    @Override
    public void notifyStop() {}

    public class SessionProviderHttpServletRequestWrapper extends HttpServletRequestWrapper {

        protected HttpSession session;

        protected HttpServletRequest req;
        protected HttpServletResponse resp;
        protected ServletContext servletContext;

        public SessionProviderHttpServletRequestWrapper(HttpServletRequest req,
                                                        HttpServletResponse resp,
                                                        ServletContext servletContext) {
            super(req);
            this.req = req;
            this.resp = resp;
            this.servletContext = servletContext;
            this.session = getExistSession(req, resp, servletContext);
        }

        @Override
        public HttpSession getSession(boolean create) {
            if (create && session == null) {
                synchronized (lock) {// 因为创建Session并不需要太多并发
                    if (session == null) {
                        session = createSession(req, resp, servletContext);
                    }
                }
            }
            return session;
        }

        @Override
        public HttpSession getSession() {
            return getSession(true);
        }
    }
}
