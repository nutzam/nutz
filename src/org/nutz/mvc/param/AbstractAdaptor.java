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

public abstract class AbstractAdaptor implements HttpAdaptor {

	protected ParamInjector[] injs;

	public void init(Method method) {
		Class<?>[] argTypes = method.getParameterTypes();
		injs = new ParamInjector[argTypes.length];
		Annotation[][] annss = method.getParameterAnnotations();
		for (int i = 0; i < annss.length; i++) {
			Annotation[] anns = annss[i];
			Param param = null;
			// find @Param in current annotations
			for (int x = 0; x < anns.length; x++)
				if (anns[x] instanceof Param) {
					param = (Param) anns[x];
					break;
				}
			// Store
			injs[i] = evalDefaultInjector(argTypes[i]);
			if (null == injs[i])
				injs[i] = evalInjector(argTypes[i], param);
			if (null == injs[i])
				throw Lang.makeThrow("Don't know how to inject %s.%s(...[%d]%s %s...),", method
						.getDeclaringClass(), method.getName(), i, argTypes[i].getName(), param);
		}
	}

	private static ParamInjector evalDefaultInjector(Class<?> type) {
		// Request
		if (ServletRequest.class.isAssignableFrom(type)) {
			return new RequestInjector();
		}
		// Response
		else if (ServletResponse.class.isAssignableFrom(type)) {
			return new ResponseInjector();
		}
		// Session
		else if (HttpSession.class.isAssignableFrom(type)) {
			return new SessionInjector();
		}
		// ServletContext
		else if (ServletContext.class.isAssignableFrom(type)) {
			return new ServletContextInjector();
		}
		// Ioc
		else if (Ioc.class.isAssignableFrom(type)) {
			return new IocInjector();
		}
		return null;
	}

	protected abstract ParamInjector evalInjector(Class<?> type, Param param);

	public Object[] adapt(HttpServletRequest request, HttpServletResponse response) {
		Object[] args = new Object[injs.length];
		for (int i = 0; i < injs.length; i++) {
			args[i] = injs[i].get(request, response, null);
		}
		return args;
	}

}
