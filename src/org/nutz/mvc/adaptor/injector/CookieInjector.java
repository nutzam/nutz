package org.nutz.mvc.adaptor.injector;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.ParamInjector;

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
        if (_cookies == null)
            _cookies = new Cookie[0];
		if ("_map".equals(name)) {
			Map<String, String> cookies = new LinkedHashMap<String, String>();
			for (Cookie cookie : _cookies) {
				cookies.put(cookie.getName(), cookie.getValue());
			}
			return cookies;
		}
		for (Cookie cookie : _cookies) {
			if (cookie.getName().equalsIgnoreCase(name))
				return Castors.me().castTo(cookie.getValue(), type);
		}
		return null;
	}

}
