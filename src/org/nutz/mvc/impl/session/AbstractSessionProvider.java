package org.nutz.mvc.impl.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.mvc.SessionProvider;

/**
 * 抽象的SessionProvider实现,可以作为
 * @author wendal
 *
 */
public abstract class AbstractSessionProvider implements SessionProvider {
	
	private static final Object lock = new Object();
	
	public HttpServletRequest filter(final HttpServletRequest req,
									 final HttpServletResponse resp,
									 final ServletContext servletContext) {
		return new HttpServletRequestWrapper(req) {
			
			private HttpSession session;

			public HttpSession getSession(boolean create) {
				if (create && session == null) {
					synchronized (lock) {//因为创建Session并不需要太多并发
						if (session == null)
							session = createSession(req, resp, servletContext);
					}
				}
				return session;
			}
		};
	}
	
	/**
	 * 子类覆盖此方法,以创建一个新的Session对象,或者任何你想做的事
	 */
	public abstract HttpSession createSession(final HttpServletRequest req,
									 final HttpServletResponse resp,
									 final ServletContext servletContext);
	
	public void notifyStop() {
	}
	
}
