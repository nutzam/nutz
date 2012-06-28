package org.nutz.mvc.upload.injector;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

public class MapArrayInjector implements ParamInjector {

    public MapArrayInjector(Class<?> eleType, String name) {
        this.eleType = eleType;
        this.name = name;
    }

    private Class<?> eleType;

    private String name;

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        Object obj = ((Map<?, ?>) refer).get(name);
        if (obj == null)
            return null;

        if (obj instanceof List) {
            return Lang.collection2array((List<?>) ((List<?>) obj));
        }

        Object re = Array.newInstance(eleType, 1);
        Array.set(re, 0, obj);
        return re;
    }

}
