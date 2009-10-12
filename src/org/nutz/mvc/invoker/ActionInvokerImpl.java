package org.nutz.mvc.invoker;

import java.io.IOException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.param.PairHttpAdaptor;
import org.nutz.mvc.view.UTF8JsonView;

public class ActionInvokerImpl implements ActionInvoker {

	private Object obj;
	private Method method;
	private View ok;
	private View fail;
	private HttpAdaptor adaptor;

	public ActionInvokerImpl(List<ViewMaker> makers, Object obj, Method method, Ok dftOk,
			Fail dftFail, AdaptBy dftAb) {
		this.obj = obj;
		this.method = method;
		this.ok = evalView(makers, method.getAnnotation(Ok.class), dftOk);
		this.fail = evalView(makers, method.getAnnotation(Fail.class), dftFail);
		evalHttpAdaptor(method, dftAb);
	}

	private void evalHttpAdaptor(Method method, AdaptBy dftAb) {
		AdaptBy ab = method.getAnnotation(AdaptBy.class);
		try {
			if (null != ab) {
				adaptor = Mirror.me(ab.value()).born((Object[]) ab.args());
			} else if (null != dftAb) {
				adaptor = Mirror.me(dftAb.value()).born((Object[]) dftAb.args());
			} else {
				adaptor = new PairHttpAdaptor();
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		adaptor.init(method);
	}

	private <T extends Annotation> View evalView(List<ViewMaker> makers, T ann, T dft) {
		if (ann == null)
			ann = dft;
		if (ann == null)
			return new UTF8JsonView(JsonFormat.compact());

		String str = (String) Mirror.me(ann.getClass()).invoke(ann, "value");
		int pos = str.indexOf(':');
		String type, value;
		if (pos > 0) {
			type = Strings.trim(str.substring(0, pos).toLowerCase());
			value = Strings.trim(pos >= (str.length() - 1) ? null : str.substring(pos + 1));
		} else {
			type = str;
			value = null;
		}
		for (ViewMaker maker : makers) {
			View view = maker.make(type, value);
			if (null != view)
				return view;
		}
		return null;
	}

	public void invoke(HttpServletRequest request, HttpServletResponse response) {
		Object[] args = adaptor.adapt(request, response);
		Object re;
		try {
			re = method.invoke(obj, args);
			if (re instanceof View)
				((View) re).render(request, response, re);
			else
				ok.render(request, response, re);
		} catch (Throwable e) {
			try {
				fail.render(request, response, e);
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
