package org.nutz.mvc.impl.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 使用容器原生的Session实现 == 等于什么都没做.
 *
 */
public class NopSessionProvider extends AbstractSessionProvider {

	public HttpSession createSession(HttpServletRequest req,
									 HttpServletResponse resp,
									 ServletContext servletContext) {
		//使用容器原生的Session实现 == 等于什么都没做
		return req.getSession(true);
	}

}
