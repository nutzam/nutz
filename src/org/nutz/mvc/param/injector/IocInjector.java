package org.nutz.mvc.param.injector;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Mvcs;
import org.nutz.mvc.param.ParamInjector;

public class IocInjector implements ParamInjector {

	public Object get(HttpServletRequest request, HttpServletResponse response,
			Object refer) {
		return Mvcs.getIoc(request);
	}

}
