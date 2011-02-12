package org.nutz.mvc.invoker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.impl.ComboContext;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInvoker;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.ioc.RequestIocContext;
import org.nutz.mvc.ioc.SessionIocContext;

public class ActionInvokerImpl2 implements ActionInvoker {

	private static final Log log = Logs.getLog(ActionInvokerImpl2.class);

	public String moduleName;
	public Class<?> moduleType;
	public Object module;
	public Method method;
	public View ok;
	public View fail;
	public HttpAdaptor adaptor;
	public ActionFilter[] filters;
	public String inputCharset;
	public String outputCharset;

	public ActionInvokerImpl2(Method method) {
		this.method = method;
	}
	

	public void invoke(	ServletContext sc,
						HttpServletRequest req,
						HttpServletResponse resp,
						String[] pathArgs) {
		// setup the charset
		try {
			req.setCharacterEncoding(inputCharset);
		}
		catch (UnsupportedEncodingException e) {
			if (log.isWarnEnabled())
				log.warn(getExceptionMessage(e), e);
			throw Lang.wrapThrow(e);
		}
		resp.setCharacterEncoding(outputCharset);

		// Before adapt, run filter
		if (null != filters)
			for (ActionFilter filter : filters) {
				View view = filter.match(sc, req, method);
				if (null != view) {
					try {
						view.render(req, resp, null);
					}
					catch (Throwable e) {
						if (log.isWarnEnabled())
							log.warn(getExceptionMessage(e), e);
						throw Lang.wrapThrow(e);
					}
					return;
				}
			}
		// If all filters return null, then going on...
		RequestIocContext reqContext = null;
		try {
			Object obj;
			// 判断 moudle 是否存在在 Ioc 容器中
			// P.S. 这个分支可以考虑用一个 Flyweight 封装一下，代码可能会更干净点
			if (null != module) {
				obj = module;
			} else {
				Ioc ioc = Mvcs.getIoc(sc);
				if (null == ioc)
					throw Lang.makeThrow(	"Moudle with @InjectName('%s') but you not declare a Ioc for this app",
											moduleName);
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
				else
					obj = ioc.get(moduleType, moduleName);
			}
			
			Object[] args = adaptor.adapt(sc, req, resp, pathArgs);
			// 调用 module 中的方法
			Object re = method.invoke(obj, args);

			// 渲染 HTTP 输出流
			if (re instanceof View)
				((View) re).render(req, resp, null);
			else
				ok.render(req, resp, re);
		}
		// 如果有错误，则转到失败渲染流程
		catch (Throwable e) {
			// 基本上， InvocationTargetException 一点意义也没有，需要拆包
			if (e instanceof InvocationTargetException && e.getCause() != null)
				e = e.getCause();

			// 在 Debug 模式下，输出这个错误信息到日志里有助于调试
			if (log.isDebugEnabled())
				log.debug(getExceptionMessage(e), e);

			try {
				fail.render(req, resp, e);
			}
			// 失败渲染流程也失败的话，则试图直接渲染一下失败信息
			catch (Throwable e1) {
				// 打印 Log
				if (log.isWarnEnabled())
					log.warn(getExceptionMessage(e1), e1);

				resp.reset();
				try {
					resp.getWriter().write(e1.getMessage());
					resp.flushBuffer();
				}
				// 仍然失败？ 没办法，抛出异常吧
				catch (IOException e2) {
					// 打印 Log
					if (log.isWarnEnabled())
						log.warn(getExceptionMessage(e2), e2);

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

	private static final String getExceptionMessage(Throwable e) {
		e = Lang.unwrapThrow(e);
		return Strings.isBlank(e.getMessage()) ? e.getClass().getSimpleName() : e.getMessage();
	}
}
