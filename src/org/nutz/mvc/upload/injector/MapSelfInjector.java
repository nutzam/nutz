package org.nutz.mvc.upload.injector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;

public class MapSelfInjector implements ParamInjector {

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return refer;
    }

}
