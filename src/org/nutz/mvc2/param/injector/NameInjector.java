package org.nutz.mvc2.param.injector;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.mvc2.param.ParamInjector;

public class NameInjector implements ParamInjector {

	protected String name;
	private Class<?> type;

	protected NameInjector(String name) {
		this.name = name;
	}

	public NameInjector(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public Object get(HttpServletRequest request, HttpServletResponse response,
			Object refer) {
		String value = request.getParameter(name);
		return Castors.me().castTo(value, type);
	}

}
