package org.nutz.mvc.adaptor;

import java.lang.annotation.Annotation;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.adaptor.injector.*;
import org.nutz.mvc.annotation.Param;

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
			if (null != injs[i])
				continue;
			injs[i] = evalInjector(argTypes[i], param);
			// 子类也不能确定，如何适配这个参数，那么做一个标记，如果
			// 这个参数被 ParamInjector 适配到，就会抛错。
			// 这个设计是因为了 "路径参数"
			if (null == injs[i])
				injs[i] = new ErrorInjector(method, i);
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

	public Object[] adapt(	HttpServletRequest request,
							HttpServletResponse response,
							String[] pathArgs) {
		Object[] args = new Object[injs.length];
		int i = fillPathArgs(request, response, pathArgs, args);
		// Inject another params
		for (; i < injs.length; i++) {
			args[i] = injs[i].get(request, response, null);
		}
		return args;
	}

	protected int fillPathArgs(	HttpServletRequest request,
								HttpServletResponse response,
								String[] pathArgs,
								Object[] args) {
		int i = 0;
		// Loop path args
		if (null != pathArgs) {
			int len = Math.min(args.length, pathArgs.length);
			for (; i < len; i++)
				args[i] = injs[i].get(request, response, pathArgs[i]);
		}
		return i;
	}

}
