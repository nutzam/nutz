package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActionInvoker {

	void invoke(HttpServletRequest req, HttpServletResponse resp, String[] pathArgs);

}
