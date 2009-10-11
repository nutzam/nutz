package org.nutz.mvc2.param;

import java.lang.annotation.Annotation;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.mvc2.HttpAdaptor;
import org.nutz.mvc2.Mvcs;
import org.nutz.mvc2.annotation.Param;

public abstract class AbstractHttpAdaptor implements HttpAdaptor {

	protected ParamBean[] params;

	public void init(Method method) {
		Class<?>[] argTypes = method.getParameterTypes();
		params = new ParamBean[argTypes.length];
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
			// Store bean
			params[i] = new ParamBean(argTypes[i], name);
		}
	}

	protected static boolean isNeedSkip(HttpServletRequest request, HttpServletResponse response, Object[] args, int i,
			ParamBean p) {
		// Request
		if (p.type.isAssignableFrom(ServletRequest.class)) {
			args[i] = request;
			return true;
		}
		// Response
		else if (p.type.isAssignableFrom(ServletResponse.class)) {
			args[i] = response;
			return true;
		}
		// Session
		else if (p.type.isAssignableFrom(HttpSession.class)) {
			args[i] = request.getSession();
			return true;
		}
		// ServletContext
		else if (p.type.isAssignableFrom(ServletContext.class)) {
			args[i] = request.getSession().getServletContext();
			return true;
		}
		// Ioc
		else if (p.type.isAssignableFrom(Ioc.class)) {
			args[i] = Mvcs.getIoc(request);
			return true;
		}
		return false;
	}

}
