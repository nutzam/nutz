package org.nutz.mvc.invoker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.param.PairAdaptor;
import org.nutz.mvc.view.UTF8JsonView;

public class ActionInvokerImpl implements ActionInvoker {

	private Object obj;
	private Method method;
	private View ok;
	private View fail;
	private HttpAdaptor adaptor;
	private ActionFilter[] filters;
	private String inputCharset;
	private String outputCharset;

	public ActionInvokerImpl(	Ioc ioc,
								List<ViewMaker> makers,
								Object obj,
								Method method,
								Ok dftOk,
								Fail dftFail,
								AdaptBy dftAb,
								Filters dftflts,
								Encoding dftEncoding) {
		this.obj = obj;
		this.method = method;
		this.ok = evalView(ioc, makers, method.getAnnotation(Ok.class), dftOk);
		this.fail = evalView(ioc, makers, method.getAnnotation(Fail.class), dftFail);
		this.evalHttpAdaptor(ioc, method, dftAb);
		this.evalFilters(ioc, method, dftflts);
		this.evalEncoding(method, dftEncoding);
	}

	private void evalEncoding(Method method, Encoding dftEncoding) {
		Encoding encoding = method.getAnnotation(Encoding.class);
		if (null == encoding)
			encoding = dftEncoding;
		if (null == encoding) {
			inputCharset = "UTF-8";
			outputCharset = "UTF-8";
		} else {
			inputCharset = encoding.input();
			outputCharset = encoding.output();
		}
	}

	private void evalFilters(Ioc ioc, Method method, Filters dftflts) {
		Filters flts = method.getAnnotation(Filters.class);
		if (null == flts)
			flts = dftflts;
		if (null != flts) {
			filters = new ActionFilter[flts.value().length];
			for (int i = 0; i < this.filters.length; i++) {
				By by = flts.value()[i];
				filters[i] = evalObject(ioc, by.type(), by.args());
			}
		}
	}

	private void evalHttpAdaptor(Ioc ioc, Method method, AdaptBy dftAb) {
		AdaptBy ab = method.getAnnotation(AdaptBy.class);
		try {
			if (null != ab) {
				adaptor = evalObject(ioc, ab.type(), ab.args());
			} else if (null != dftAb) {
				adaptor = evalObject(ioc, dftAb.type(), dftAb.args());
			} else {
				adaptor = new PairAdaptor();
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		adaptor.init(method);
	}

	private static <T> T evalObject(Ioc ioc, Class<T> type, String[] args) {
		/*
		 * 如果参数的形式为: {"ioc:xxx"}，则用 ioc.get(type,"xxx") 获取这个适配器
		 */
		if (null != ioc && null != args && args.length == 1 && !Strings.isBlank(args[0])) {
			int pos = args[0].indexOf(':');
			if (pos == 3 && pos < (args[0].length() - 1)
					&& "ioc".equalsIgnoreCase(args[0].substring(0, pos))) {
				String name = args[0].substring(pos);
				return ioc.get(type, name);
			}
		}
		return Mirror.me(type).born((Object[]) args);
	}

	private <T extends Annotation> View evalView(Ioc ioc, List<ViewMaker> makers, T ann, T dft) {
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
			View view = maker.make(ioc, type, value);
			if (null != view)
				return view;
		}
		return null;
	}

	public void invoke(HttpServletRequest req, HttpServletResponse resp) {
		// setup the charset
		try {
			req.setCharacterEncoding(inputCharset);
		} catch (UnsupportedEncodingException e3) {
			throw Lang.wrapThrow(e3);
		}
		resp.setCharacterEncoding(outputCharset);

		// Before adapt, run filter
		if (null != filters)
			for (ActionFilter filter : filters) {
				View view = filter.match(req);
				if (null != view) {
					try {
						view.render(req, resp, null);
					} catch (Throwable e) {
						throw Lang.wrapThrow(e);
					}
					return;
				}
			}
		// If all filter return null, then going on...
		Object[] args = adaptor.adapt(req, resp);
		Object re;
		try {
			re = method.invoke(obj, args);
			if (re instanceof View)
				((View) re).render(req, resp, re);
			else
				ok.render(req, resp, re);
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException)
				e = e.getCause();
			try {
				fail.render(req, resp, e);
			} catch (Throwable e1) {
				resp.reset();
				try {
					resp.getWriter().write(e1.getMessage());
					resp.flushBuffer();
				} catch (IOException e2) {
					throw Lang.wrapThrow(e2);
				}
			}
		}

	}

}
