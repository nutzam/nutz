package org.nutz.mvc.adaptor.injector;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;

public class ReqHeaderInjector implements ParamInjector {

	private String name;
	
	public ReqHeaderInjector(String name) {
		this.name = name;
	}
	
	public Object get(ServletContext sc,
					  HttpServletRequest req,
					  HttpServletResponse resp,
					  Object refer) {
		if ("_map".equals(name)) {
			Map<String, String> headers = new LinkedHashMap<String, String>();
			Enumeration<String> names = req.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				headers.put(name, req.getHeader(name));
			}
			return headers;
		}
		return req.getHeader(name);
	}

}
