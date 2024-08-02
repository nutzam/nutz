package org.nutz.mvc.adaptor.injector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestAttrInjector extends AttrInjector {

    public RequestAttrInjector(String name) {
        super(name);
    }

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return req.getAttribute(name);
    }

}
