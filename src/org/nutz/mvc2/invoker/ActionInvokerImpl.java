package org.nutz.mvc2.invoker;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc2.ActionInvoker;
import org.nutz.mvc2.View;
import org.nutz.mvc2.annotation.Views;

public class ActionInvokerImpl implements ActionInvoker {

	private Object obj;
	private Method method;
	private View ok;
	private View error;

	public ActionInvokerImpl(Object obj, Method method, Views defaultView) {
		this.obj = obj;
		this.method = method;
	}

	public void invoke(HttpServletRequest request, HttpServletResponse response) {
		Object args = null;
		Object re;
		try {
			re = method.invoke(obj, args);
			if (re instanceof View)
				((View) re).render(request, response, re);
			else
				ok.render(request, response, re);
		} catch (Throwable e) {
			try {
				error.render(request, response, e);
			} catch (Throwable e1) {
				response.reset();
				try {
					response.getWriter().write(e1.getMessage());
					response.flushBuffer();
				} catch (IOException e2) {
					throw Lang.wrapThrow(e2);
				}
			}
		}

	}

}
