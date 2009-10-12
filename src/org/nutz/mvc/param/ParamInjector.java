package org.nutz.mvc.param;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ParamInjector {

	Object get(HttpServletRequest request, HttpServletResponse response, Object refer);

}
