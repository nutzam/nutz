package org.nutz.mvc.param.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mvc.param.ParamInjector;

public class NameInjector implements ParamInjector {

	protected String name;
	protected Class<?> type;

	protected NameInjector(String name) {
		if (null == name)
			throw Lang.makeThrow("Can not accept null as name, type '%s'", type.getName());
		this.name = name;
	}

	public NameInjector(String name, Class<?> type) {
		this(name);
		this.type = type;
	}

	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		String value = request.getParameter(name);
		return Castors.me().castTo(value, type);
	}

}
