package org.nutz.mvc.adaptor.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppAttrInjector extends AttrInjector {

	public AppAttrInjector(String name) {
		super(name);
	}

	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		return req.getSession().getServletContext().getAttribute(name);
	}

}
