package org.nutz.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActionInvoker {

	void invoke(ServletContext sc,
				HttpServletRequest req,
				HttpServletResponse resp,
				String[] pathArgs);

}
