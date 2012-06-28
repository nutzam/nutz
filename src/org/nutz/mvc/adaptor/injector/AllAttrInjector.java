package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Mvcs;

public class AllAttrInjector extends AttrInjector {

    public AllAttrInjector(String name) {
        super(name);
    }

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        Object re = req.getAttribute(name);
        if (null != re)
            return re;
        re = Mvcs.getHttpSession().getAttribute(name);
        if (null != re)
            return re;
        return sc.getAttribute(name);
    }

}
