package org.nutz.mvc.param.injector;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.param.ParamInjector;

public class ErrorInjector implements ParamInjector {

	private Method method;
	private int index;

	public ErrorInjector(Method method, int index) {
		this.method = method;
		this.index = index;
	}

	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		throw Lang.makeThrow("Don't know how to inject %s.%s(...[%d]%s...),", method
				.getDeclaringClass(), method.getName(), index, method.getParameterTypes()[index]);
	}

}
