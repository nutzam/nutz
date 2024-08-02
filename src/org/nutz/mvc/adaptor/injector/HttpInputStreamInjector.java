package org.nutz.mvc.adaptor.injector;

import java.io.IOException;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpInputStreamInjector implements ParamInjector {

    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        try {
            return req.getInputStream();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
