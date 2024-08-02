package org.nutz.mvc.upload.injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MapListInjector implements ParamInjector {

    public MapListInjector(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        if (refer == null) {
            return null;
        }
        Object obj = ((Map<?, ?>) refer).get(name);
        if (obj == null) {
            return null;
        }

        if (obj instanceof List) {
            return obj;
        }

        List<Object> re = new ArrayList<Object>(1);
        re.add(obj);
        return re;
    }

}
