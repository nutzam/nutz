package org.nutz.mvc;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.config.AtMap;
import org.nutz.mvc.ioc.SessionIocContext;

/**
 * Mvc 相关帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Mvcs {

	public static final String DEFAULT_MSGS = "$default";
	public static final String MSG = "msg";
	public static final String LOCALE_NAME = "nutz.mvc.locale";

	/**
	 * 从 Request 里获取一个 Ioc 容器
	 * <p>
	 * 1.a.33 之后不推荐采用
	 * 
	 * @param request
	 *            请求对象
	 * 
	 * @return Ioc 容器
	 * 
	 * @see org.nutz.mvc.annotation.IocBy
	 */
	@Deprecated
	public static Ioc getIoc(HttpServletRequest request) {
		return getIoc(request.getSession().getServletContext());
	}

	/**
	 * 从 ServletContext 里获取一个 Ioc 容器
	 * 
	 * @param context
	 *            上下文环境
	 * 
	 * @return Ioc 容器
	 * 
	 * @see org.nutz.mvc.annotation.IocBy
	 */
	public static Ioc getIoc(ServletContext context) {
		return (Ioc) context.getAttribute(Ioc.class.getName());
	}

	public static AtMap getAtMap(ServletContext context) {
		return (AtMap) context.getAttribute(AtMap.class.getName());
	}

	/**
	 * 获取当前会话的 Locale 名称
	 */
	public static String getLocaleName(HttpSession session) {
		return (String) session.getAttribute(LOCALE_NAME);
	}

	/**
	 * 为当前会话设置 Locale 的名称
	 */
	public static void setLocaleName(HttpSession session, String name) {
		session.setAttribute(LOCALE_NAME, name);
		session.removeAttribute(MSG);
	}

	/**
	 * 判断当前会话是够设置了特殊的 Locale 的名称。
	 */
	public static boolean hasLocaleName(HttpSession session) {
		return Strings.isBlank(getLocaleName(session));
	}

	/**
	 * 判断当前会话是否已经设置了本地字符串表
	 */
	public static boolean hasLocale(HttpSession session) {
		return null != session.getAttribute(MSG);
	}

	/**
	 * 获取整个应用可用的 Locale 名称集合
	 */
	public static Set<String> getLocaleNames(ServletContext context) {
		Map<String, Map<String, String>> msgss = getMessageSet(context);
		if (null != msgss)
			return msgss.keySet();
		return null;
	}

	/**
	 * 为当前会话设置本地字符串表。如果传入的 localeName 为空，或者不存在该表，则用默认字符串表替代。 执行完毕后，在 session
	 * 中会有一个属性（名称请参见 Mvcs.MSGS 定义）
	 * <p>
	 * 在 jsp 中，你可以用 EL 表达式 ${msgs.xxx} 来直接获取字符串的值。
	 * 
	 * @return 设置的 本地化字符串表
	 */
	public static Map<String, String> setLocale(HttpSession session,
			String localeName) {
		Map<String, Map<String, String>> msgss = getMessageSet(session
				.getServletContext());
		if (null != msgss) {
			Map<String, String> msgs = null;
			if (null != localeName)
				msgs = msgss.get(localeName);
			if (null == msgs)
				msgs = msgss.get(DEFAULT_MSGS);

			if (null != msgs)
				session.setAttribute(MSG, msgs);

			return msgs;
		}
		return null;
	}

	/**
	 * 获取某一个本地字符串表
	 * 
	 * @param context
	 *            上下文
	 * @param localeName
	 *            本地名
	 * @return 字符串表
	 * 
	 * @see org.nutz.mvc.annotation.Localization
	 * @see org.nutz.mvc.MessageLoader
	 */
	public static Map<String, String> getLocaleMessage(ServletContext context,
			String localeName) {
		Map<String, Map<String, String>> msgss = getMessageSet(context);
		if (null != msgss)
			return msgss.get(localeName);
		return null;
	}

	/**
	 * 获取整个应用的默认字符串表
	 * 
	 * @param context
	 *            上下文
	 * @return 字符串表
	 */
	public static Map<String, String> getDefaultLocaleMessage(
			ServletContext context) {
		Map<String, Map<String, String>> msgss = getMessageSet(context);
		if (null != msgss)
			return msgss.get(DEFAULT_MSGS);
		return null;
	}

	/**
	 * 获取整个应用的字符串表集合
	 * 
	 * @param context
	 *            上下文
	 * @return 字符串表集合
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, String>> getMessageSet(
			ServletContext context) {
		return (Map<String, Map<String, String>>) context
				.getAttribute(Localization.class.getName());
	}

	/**
	 * 获取当前请求对象的字符串表
	 * 
	 * @param req
	 *            请求对象
	 * @return 字符串表
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getMessages(ServletRequest req) {
		return (Map<String, String>) req.getAttribute(MSG);
	}

	/**
	 * 获取当前请求对象的字符串表中的某一个字符串
	 * 
	 * @param req
	 *            请求对象
	 * @param key
	 *            字符串键值
	 * @return 字符串内容
	 */
	public static String getMessage(ServletRequest req, String key) {
		Map<String, String> map = getMessages(req);
		if (null != map)
			return map.get(key);
		return null;
	}

	/**
	 * 为当前的 HTTP 请求对象设置一些必要的属性。包括：
	 * <ul>
	 * <li>本地化子字符串 => ${msg}
	 * <li>应用的路径名 => ${base}
	 * </ul>
	 * 
	 * @param req
	 *            HTTP 请求对象
	 */
	@SuppressWarnings("unchecked")
	public static void updateRequestAttributes(HttpServletRequest req) {
		if (null != req.getSession().getServletContext()
				.getAttribute(Localization.class.getName())) {
			HttpSession session = req.getSession();
			Map<String, String> msgs = null;
			if (!hasLocale(session))
				msgs = setLocale(session, getLocaleName(session));
			else
				msgs = (Map<String, String>) session.getAttribute(MSG);
			req.setAttribute(MSG, msgs);
		}
		// Update context path each time, Servlet 2.4 and early don't support
		// ServletContext.getContextPath()
		req.setAttribute("base", req.getContextPath());
	}

	/**
	 * 获取当前请求的路径，并去掉后缀
	 */
	public static String getRequestPath(HttpServletRequest req) {
		return getRequestPathObject(req).getPath();
	}

	/**
	 * 获取当前请求的路径，并去掉后缀
	 */
	public static RequestPath getRequestPathObject(HttpServletRequest req) {
		String url = req.getPathInfo();
		if (null == url)
			url = req.getServletPath();
		return getRequestPathObject(url);
	}

	/**
	 * 获取当前请求的路径，并去掉后缀
	 */
	public static RequestPath getRequestPathObject(String url) {
		RequestPath rr = new RequestPath();
		rr.setUrl(url);
		if (null != url) {
			int lio = 0;
			if (!url.endsWith("/")) {
				int ll = url.lastIndexOf('/');
				lio = url.lastIndexOf('.');
				if (lio < ll)
					lio = -1;
			}
			if (lio > 0) {
				rr.setPath(url.substring(0, lio));
				rr.setSuffix(url.substring(lio + 1));
			} else {
				rr.setPath(url);
				rr.setSuffix("");
			}
		} else {
			rr.setPath("");
			rr.setSuffix("");
		}
		return rr;
	}

	/**
	 * 注销当前 HTTP 会话。所有 Ioc 容器存入的对象都会被注销
	 * 
	 * @param session
	 *            HTTP 会话对象
	 */
	public static void deposeSession(HttpSession session) {
		if (session != null)
			new SessionIocContext(session).depose();
	}

	/**
	 * 它将对象序列化成 JSON 字符串，并写入 HTTP 响应
	 * 
	 * @param resp
	 *            响应对象
	 * @param obj
	 *            数据对象
	 * @param format
	 *            JSON 的格式化方式
	 * @throws IOException
	 *             写入失败
	 */
	public static void write(HttpServletResponse resp, Object obj,
			JsonFormat format) throws IOException {
		resp.setHeader("Cache-Control", "no-cache");
		resp.setContentType("text/plain");

		// by mawm 改为直接采用resp.getWriter()的方式直接输出!
		Json.toJson(resp.getWriter(), obj, format);

		resp.flushBuffer();
	}
}
