package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface SessionProvider {

	HttpSession getHttpSession(HttpServletRequest req);

	HttpSession getHttpSession(HttpServletRequest req, boolean createNew);

	void notifyStop();
}
