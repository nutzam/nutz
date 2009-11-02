package org.nutz.mvc;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.Localization;

public class Mvcs {

	public static final String DEFAULT_MSGS = "$default";
	public static final String MSG = "msg";
	public static final String LOCALE_NAME = "nutz.mvc.locale";

	public static Ioc getIoc(HttpServletRequest request) {
		return getIoc(request.getSession().getServletContext());
	}

	public static Ioc getIoc(ServletContext context) {
		return (Ioc) context.getAttribute(Ioc.class.getName());
	}

	public static UrlMap getUrls(ServletContext context) {
		return (UrlMap) context.getAttribute(UrlMap.class.getName());
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
	@SuppressWarnings("unchecked")
	public static Set<String> getLocaleNames(ServletContext context) {
		Map<String, Map<String, String>> msgss = (Map<String, Map<String, String>>) context
				.getAttribute(Localization.class.getName());
		if (null != msgss)
			return msgss.keySet();
		return null;
	}

	/**
	 * 为当前会话设置本地字符串表。如果传入的 localeName 为空，或者不存在该表，则用默认字符串表替代。 执行完毕后，在 session
	 * 中会有一个属性（名称请参见 Mvcs.MSGS 定义）
	 * <p>
	 * 在 jsp 中，你可以用 EL 表达式 ${msgs.xxx} 来直接获取字符串的值。
	 */
	@SuppressWarnings("unchecked")
	public static void setLocale(HttpSession session, String localeName) {
		Map<String, Map<String, String>> msgss = (Map<String, Map<String, String>>) session
				.getServletContext().getAttribute(Localization.class.getName());
		if (null != msgss) {
			Map<String, String> msgs = null;
			if (null != localeName)
				msgs = msgss.get(localeName);
			if (null == msgs)
				msgs = msgss.get(DEFAULT_MSGS);

			if (null != msgs)
				session.setAttribute(MSG, msgs);
		}
	}

	static void updateRequestAttributes(HttpServletRequest req) {
		if (null != req.getSession().getServletContext().getAttribute(Localization.class.getName())) {
			HttpSession session = req.getSession();
			if (!hasLocale(session))
				setLocale(session, getLocaleName(session));
		}
		// Store context path as "/XXXX", you can use it in JSP page
		req.setAttribute("base", req.getContextPath());
	}

	public static String getRequestPath(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (null == path)
			path = req.getServletPath();
		int lio = path.lastIndexOf('.');
		if (lio > 0)
			path = path.substring(0, lio);
		return path;
	}
}
