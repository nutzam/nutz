package org.nutz.mvc.adaptor.injector;

import org.nutz.mvc.Mvcs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AllAttrInjector extends AttrInjector {

    public AllAttrInjector(String name) {
        super(name);
    }

    @Override
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        Object re = req.getAttribute(name);
        if (null != re) {
            return re;
        }
        HttpSession session = Mvcs.getHttpSession(false);
        if (session != null) {
            re = session.getAttribute(name);
            if (null != re) {
                return re;
            }
        }

        return sc.getAttribute(name);
    }

}
