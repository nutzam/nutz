package org.nutz.http.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface SessionManger {

	HttpSession get(HttpServletRequest req, boolean createIfNotExist);
	
	void kill(NutHttpSession nutHttpSession);
}
