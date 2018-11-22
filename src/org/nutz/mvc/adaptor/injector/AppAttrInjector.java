package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppAttrInjector extends AttrInjector {

    public AppAttrInjector(String name) {
        super(name);
    }

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return sc.getAttribute(name);
    }

}
