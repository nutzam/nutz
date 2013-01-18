package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.mvc.Mvcs;

public class SessionAttrInjector extends AttrInjector {

    public SessionAttrInjector(String name) {
        super(name);
    }

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        HttpSession session = Mvcs.getHttpSession(false);
        if (session == null)
        	return null;
    	return session.getAttribute(name);
    }

}
