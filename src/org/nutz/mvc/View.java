package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface View {

	void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Throwable;
	
}
