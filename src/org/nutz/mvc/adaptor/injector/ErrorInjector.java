package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Method;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ErrorInjector implements ParamInjector {

    private Method method;
    private int index;

    public ErrorInjector(Method method, int index) {
        this.method = method;
        this.index = index;
    }

    @Override
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        throw Lang.makeThrow("Don't know how to inject %s.%s(...[%d]%s...),",
                             method.getDeclaringClass(),
                             method.getName(),
                             index,
                             method.getParameterTypes()[index]);
    }

}
