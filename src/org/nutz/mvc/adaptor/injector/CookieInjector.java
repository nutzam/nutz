package org.nutz.mvc.adaptor.injector;

import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieInjector implements ParamInjector {

    private String name;

    private Class<?> type;

    public CookieInjector(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        Cookie[] _cookies = req.getCookies();
        if (_cookies == null) {
            _cookies = new Cookie[0];
        }
        if ("_map".equals(name)) {
            Map<String, String> cookies = new LinkedHashMap<String, String>();
            for (Cookie cookie : _cookies) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
            return cookies;
        }
        for (Cookie cookie : _cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return Castors.me().castTo(cookie.getValue(), type);
            }
        }
        return null;
    }

}
