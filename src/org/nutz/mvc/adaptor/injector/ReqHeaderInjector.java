package org.nutz.mvc.adaptor.injector;

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
		return req.getHeader(name);
	}

}
