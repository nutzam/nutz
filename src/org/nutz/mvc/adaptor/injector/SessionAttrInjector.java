package org.nutz.mvc.adaptor.injector;

import org.nutz.mvc.Mvcs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionAttrInjector extends AttrInjector {

    public SessionAttrInjector(String name) {
        super(name);
    }

    @Override
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        HttpSession session = Mvcs.getHttpSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute(name);
    }

}
