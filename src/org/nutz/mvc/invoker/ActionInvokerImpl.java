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
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.InjectName;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.ioc.RequestIocContext;
import org.nutz.mvc.ioc.SessionIocContext;
import org.nutz.mvc.view.UTF8JsonView;

public class ActionInvokerImpl implements ActionInvoker {

	private String moduleName;
	private Class<?> moduleType;
	private Object module;
	private Method method;
	private View ok;
	private View fail;
	private HttpAdaptor adaptor;
	private ActionFilter[] filters;
	private String inputCharset;
	private String outputCharset;

	public ActionInvokerImpl(	Ioc ioc,
								List<ViewMaker> makers,
								Class<?> moduleType,
								Method method,
								Ok dftOk,
								Fail dftFail,
								AdaptBy dftAb,
								Filters dftflts,
								Encoding dftEncoding) {
		evalModule(moduleType);
		this.method = method;
		this.ok = evalView(ioc, makers, method.getAnnotation(Ok.class), dftOk);
		this.fail = evalView(ioc, makers, method.getAnnotation(Fail.class), dftFail);
		this.evalHttpAdaptor(ioc, method, dftAb);
		this.evalFilters(ioc, method, dftflts);
		this.evalEncoding(method, dftEncoding);
	}

	private void evalModule(Class<?> moduleType) {
		this.moduleType = moduleType;
		InjectName name = moduleType.getAnnotation(InjectName.class);
		if (null != name)
			this.moduleName = name.value();
		else
			try {
				module = moduleType.newInstance();
			} catch (Exception e) {
				throw Lang.makeThrow(
						"Class '%s' should has a accessible default constructor : '%s'", moduleType
								.getName(), e.getMessage());
			}
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
		 * 如果参数的形式为: {"ioc:xxx"}，则用 ioc.get(type,"xxx") 获取这个对象
		 */
		if (null != ioc && null != args && args.length == 1 && !Strings.isBlank(args[0])) {
			int pos = args[0].indexOf(':');
			if (pos == 3 && pos < (args[0].length() - 1)
					&& "ioc".equalsIgnoreCase(args[0].substring(0, pos))) {
				String name = args[0].substring(pos+1);
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

	public void invoke(HttpServletRequest req, HttpServletResponse resp, String[] pathArgs) {
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
		// If all filters return null, then going on...
		RequestIocContext reqContext = null;
		try {
			Object[] args = adaptor.adapt(req, resp, pathArgs);
			Object obj;
			// 判断 moudle 是否存在在 Ioc 容器中
			// P.S. 这个分支可以考虑用一个 Flyweight 封装一下，代码可能会更干净点
			if (null != module) {
				obj = module;
			} else {
				Ioc ioc = Mvcs.getIoc(req);
				if (null == ioc)
					throw Lang.makeThrow(
							"Moudle with @InjectName('%s') but you not declare a Ioc for this app",
							module);
				/*
				 * 如果 Ioc 容器实现了高级接口，那么会为当前请求设置上下文对象
				 */
				if (ioc instanceof Ioc2) {
					reqContext = new RequestIocContext(req);
					SessionIocContext sessionContext = new SessionIocContext(req.getSession());
					IocContext myContext = new ComboContext(reqContext, sessionContext);
					obj = ((Ioc2) ioc).get(moduleType, moduleName, myContext);
				}
				/*
				 * 否则，则仅仅简单的从容器获取
				 */
				else {
					obj = ioc.get(moduleType, moduleName);
				}
			}
			// 调用 module 中的方法
			Object re = method.invoke(obj, args);
			
			// 渲染 HTTP 输出流
			if (re instanceof View)
				((View) re).render(req, resp, re);
			else
				ok.render(req, resp, re);
		}
		// 如果有错误，则转到失败渲染流程
		catch (Throwable e) {
			// 基本上， InvocationTargetException 一点意义也没有，需要拆包
			if (e instanceof InvocationTargetException)
				e = e.getCause();
			
			try {
				fail.render(req, resp, e);
			}
			// 失败渲染流程也失败的话，则试图直接渲染一下失败信息
			catch (Throwable e1) {
				resp.reset();
				try {
					resp.getWriter().write(e1.getMessage());
					resp.flushBuffer();
				}
				// 仍然失败？ 没办法，抛出异常吧
				catch (IOException e2) {
					throw Lang.wrapThrow(e2);
				}
			}
		}
		// 这里保证了，在 Request 级别的对象一定会被注销的
		finally {
			if (null != reqContext)
				reqContext.depose();
		}

	}
}
