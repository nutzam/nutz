package org.nutz.mvc.adaptor.injector;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ReqHeaderInjector implements ParamInjector {

    private String name;

    private Class<?> type;

    public ReqHeaderInjector(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        if ("_map".equals(name)) {
            Map<String, String> headers = new LinkedHashMap<String, String>();
            Enumeration<String> names = req.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                headers.put(name, req.getHeader(name));
            }
            return headers;
        }
        String val = req.getHeader(name);
        if (val == null) {
            Enumeration<String> names = req.getHeaderNames();
            while (names.hasMoreElements()) {
                String _name = names.nextElement();
                if (_name.equalsIgnoreCase(name)) {
                    val = req.getHeader(_name);
                    break;
                }
            }
            if (val == null) {
                return null;
            }
        }
        return Castors.me().castTo(val, type);
    }

}
