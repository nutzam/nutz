package org.nutz.mvc.param;

import java.lang.annotation.Annotation;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.param.injector.*;

public abstract class AbstractHttpAdaptor implements HttpAdaptor {

	protected ParamInjector[] injs;

	public void init(Method method) {
		Class<?>[] argTypes = method.getParameterTypes();
		injs = new ParamInjector[argTypes.length];
		Annotation[][] annss = method.getParameterAnnotations();
		for (int i = 0; i < annss.length; i++) {
			Annotation[] anns = annss[i];
			String name = null;
			// find @Param in current annotations
			for (int x = 0; x < anns.length; x++)
				if (anns[x] instanceof Param) {
					name = ((Param) anns[x]).value();
					break;
				}
			// Store
			injs[i] = evalDefaultInjector(argTypes[i], name);
			if (null == injs[i])
				injs[i] = evalInjector(argTypes[i], name);
			if (null == injs[i])
				throw Lang.makeThrow("Don't know how to inject %s.%s(...[%d]%s %s...),", method
						.getDeclaringClass(), method.getName(), i, argTypes[i].getName(), name);
		}
	}

	private static ParamInjector evalDefaultInjector(Class<?> type, String name) {
		// Request
		if (type.isAssignableFrom(ServletRequest.class)) {
			return new RequestInjector();
		}
		// Response
		else if (type.isAssignableFrom(ServletResponse.class)) {
			return new ResponseInjector();
		}
		// Session
		else if (type.isAssignableFrom(HttpSession.class)) {
			return new SessionInjector();
		}
		// ServletContext
		else if (type.isAssignableFrom(ServletContext.class)) {
			return new ServletContextInjector();
		}
		// Ioc
		else if (type.isAssignableFrom(Ioc.class)) {
			return new IocInjector();
		}
		return null;
	}

	protected abstract ParamInjector evalInjector(Class<?> type, String name);

	public Object[] adapt(HttpServletRequest request, HttpServletResponse response) {
		Object[] args = new Object[injs.length];
		for (int i = 0; i < injs.length; i++) {
			args[i] = injs[i].get(request, response, null);
		}
		return args;
	}

}
