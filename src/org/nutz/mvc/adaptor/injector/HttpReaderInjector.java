package org.nutz.mvc.adaptor.injector;

import java.io.IOException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

public class HttpReaderInjector implements ParamInjector {

    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        try {
            return req.getReader();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
