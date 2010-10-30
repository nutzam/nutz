package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionAttrInjector extends AttrInjector {

	public SessionAttrInjector(String name) {
		super(name);
	}

	public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
		return req.getSession().getAttribute(name);
	}

}
