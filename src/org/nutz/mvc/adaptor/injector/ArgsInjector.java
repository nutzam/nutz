package org.nutz.mvc.adaptor.injector;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.Args;
import org.nutz.mvc.adaptor.ParamInjector;

public class ArgsInjector implements ParamInjector{

	@Override
	@SuppressWarnings("unchecked")
	public Object get(ServletContext sc, HttpServletRequest req,
			HttpServletResponse resp, Object refer) {
		Enumeration<String> attrs = req.getAttributeNames();
		Enumeration<String> params = req.getParameterNames();
		Args args = new Args();
		while(params.hasMoreElements()){
			String key = params.nextElement();
			args.put(key, req.getParameter(key));
		}
		while(attrs.hasMoreElements()){
			String key = params.nextElement();
			args.put(key, req.getParameter(key));
		}
		return args;
	}

}
