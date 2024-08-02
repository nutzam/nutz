package org.nutz.mvc.adaptor.injector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AppAttrInjector extends AttrInjector {

    public AppAttrInjector(String name) {
        super(name);
    }

    @Override
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return sc.getAttribute(name);
    }

}
