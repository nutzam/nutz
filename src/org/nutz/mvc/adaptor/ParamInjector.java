package org.nutz.mvc.adaptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ParamInjector {

	Object get(HttpServletRequest req, HttpServletResponse resp, Object refer);

}
