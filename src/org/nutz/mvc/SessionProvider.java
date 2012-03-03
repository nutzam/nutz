package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface SessionProvider {

	public HttpSession getHttpSession(HttpServletRequest req);
	
	public HttpSession getHttpSession(HttpServletRequest req, boolean createNew);
}
