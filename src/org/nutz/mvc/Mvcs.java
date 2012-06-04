package org.nutz.mvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocContext;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.config.AtMap;
import org.nutz.mvc.impl.NutMessageMap;
import org.nutz.mvc.ioc.SessionIocContext;

/**
 * Mvc 相关帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class Mvcs {

	public static final String DEFAULT_MSGS = "$default";
	public static final String MSG = "msg";
	public static final String LOCALE_KEY = "nutz_mvc_localization_key";

	// 新的,基于ThreadLoacl改造过的Mvc辅助方法
	// ====================================================================

	public static Ioc getIoc() {
		return (Ioc) servletContext.getAttribute(getName() + "_ioc");
	}

	public static void setIoc(Ioc ioc) {
		servletContext.setAttribute(getName() + "_ioc", ioc);
	}

	public static AtMap getAtMap() {
		return (AtMap) servletContext.getAttribute(getName() + "_atmap");
	}

	public static void setAtMap(AtMap atmap) {
		servletContext.setAttribute(getName() + "_atmap", atmap);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, Object>> getMessageSet() {
		return (Map<String, Map<String, Object>>) servletContext.getAttribute(getName()
																				+ "_localization");
	}

	public static void setMessageSet(Map<String, Map<String, Object>> messageSet) {
		servletContext.setAttribute(getName() + "_localization", messageSet);
	}

	public static Map<String, Object> getLocaleMessage(String key) {
		Map<String, Map<String, Object>> msgss = getMessageSet();
		if (null != msgss)
			return msgss.get(key);
		return null;
	}

	public static void setNutConfig(NutConfig config) {
		getServletContext().setAttribute(getName() + "_mvc_config", config);
	}

	public static NutConfig getNutConfig() {
		return (NutConfig) getServletContext().getAttribute(getName() + "_mvc_config");
	}

	// ====================================================================

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
		return getIoc();
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
	@Deprecated
	public static Ioc getIoc(ServletContext context) {
		return getIoc();
	}

	@Deprecated
	public static AtMap getAtMap(ServletContext context) {
		return getAtMap();
	}

	/**
	 * 获取当前会话的 Locale 名称
	 */
	@Deprecated
	public static String getLocaleName(HttpSession session) {
		return (String) session.getAttribute(LOCALE_KEY);
	}

	/**
	 * 为当前会话设置 Locale 的名称
	 */
	@Deprecated
	public static void setLocaleName(HttpSession session, String name) {
		session.setAttribute(LOCALE_KEY, name);
		session.removeAttribute(MSG);
	}

	/**
	 * 判断当前会话是够设置了特殊的 Locale 的名称。
	 */
	@Deprecated
	public static boolean hasLocaleName(HttpSession session) {
		return !Strings.isBlank(getLocaleName(session));
	}

	/**
	 * 判断当前会话是否已经设置了本地字符串表
	 */
	@Deprecated
	public static boolean hasLocale(HttpSession session) {
		return null != session.getAttribute(MSG);
	}

	/**
	 * 获取整个应用可用的 Locale 名称集合
	 */
	@Deprecated
	public static Set<String> getLocaleNames(ServletContext context) {
		Map<String, Map<String, Object>> msgss = getMessageSet();
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
	@Deprecated
	public static Map<String, Object> setLocale(HttpSession session, String localeName) {
		Map<String, Map<String, Object>> msgss = getMessageSet();
		if (null != msgss) {
			Map<String, Object> msgs = null;
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
	@Deprecated
	public static Map<String, Object> getLocaleMessage(ServletContext context, String localeName) {
		Map<String, Map<String, Object>> msgss = getMessageSet();
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
	@Deprecated
	public static Map<String, Object> getDefaultLocaleMessage(ServletContext context) {
		Map<String, Map<String, Object>> msgss = getMessageSet();
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
	@Deprecated
	public static Map<String, Map<String, Object>> getMessageSet(ServletContext context) {
		return getMessageSet();
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
	 * 获取当前请求对象的字符串表（NutMessageMap 封装）
	 * 
	 * @param req
	 *            请求对象
	 * @return 字符串表
	 */
	public static NutMessageMap getMessageMap(ServletRequest req) {
		return (NutMessageMap) req.getAttribute(MSG);
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
	 * @return 当前会话的本地字符串集合的键值
	 */
	public static String getLocalizationKey() {
		HttpSession sess = getHttpSession();
		return null == sess ? null : (String) sess.getAttribute(LOCALE_KEY);
	}

	/**
	 * 设置本地话字符串的键值
	 * <p>
	 * 如果你用的是 Nutz.Mvc 默认的本地化机制，那么你的本地字符串键值，相当于一个你目录名。 <br>
	 * 比如 "zh_CN" 等
	 * 
	 * @param key
	 *            键值
	 * @return 是否设置成功
	 */
	public static boolean setLocalizationKey(String key) {
		HttpSession sess = getHttpSession();
		if (null == sess)
			return false;
		sess.setAttribute(LOCALE_KEY, key);
		return true;
	}

	/**
	 * @return 当前都加载了哪些种字符串的 key
	 */
	public static Set<String> getLocalizationKeySet() {
		Map<String, Map<String, Object>> msgss = getMessageSet();
		if (null == msgss)
			return new HashSet<String>();
		return msgss.keySet();
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
	public static void updateRequestAttributes(HttpServletRequest req) {
		// 初始化本次请求的多国语言字符串
		Map<String, Map<String, Object>> msgss = getMessageSet();
		if (null != msgss) {
			Map<String, Object> msgs = null;

			String lKey = Mvcs.getLocalizationKey();

			if (!Strings.isBlank(lKey))
				msgs = msgss.get(lKey);

			// 没有设定特殊的 Local 名字，随便取一个
			if (null == msgs) {
				if (msgss.size() > 0)
					msgs = msgss.values().iterator().next();
			}
			// 记录到请求中
			req.setAttribute(MSG, msgs);
		}

		// 记录一些数据到请求对象中
		req.setAttribute("base", req.getContextPath());
		req.setAttribute("$request", req);
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
	public static void write(HttpServletResponse resp, Object obj, JsonFormat format)
			throws IOException {
		resp.setHeader("Cache-Control", "no-cache");
		resp.setContentType("text/plain");

		// by mawm 改为直接采用resp.getWriter()的方式直接输出!
		Json.toJson(resp.getWriter(), obj, format);

		resp.flushBuffer();
	}

	// 将当前请求的主要变量保存到ThreadLocal, by wendal
	// ==================================================================
	private static final ThreadLocal<HttpServletRequest> REQ = new ThreadLocal<HttpServletRequest>();
	private static final ThreadLocal<HttpServletResponse> RESP = new ThreadLocal<HttpServletResponse>();
	private static final ThreadLocal<String> NAME = new ThreadLocal<String>();
	private static final ThreadLocal<ActionContext> ACTION_CONTEXT = new ThreadLocal<ActionContext>();
	private static final ThreadLocal<IocContext> IOC_CONTEXT = new ThreadLocal<IocContext>();

	private static ServletContext servletContext;

	public static final HttpServletRequest getReq() {
		return REQ.get();
	}

	public static final HttpServletResponse getResp() {
		return RESP.get();
	}

	public static final String getName() {
		return NAME.get();
	}

	public static final ActionContext getActionContext() {
		return ACTION_CONTEXT.get();
	}

	public static void set(String name, HttpServletRequest req, HttpServletResponse resp) {
		NAME.set(name);
		REQ.set(req);
		RESP.set(resp);
	}

	public static void setServletContext(ServletContext servletContext) {
		Mvcs.servletContext = servletContext;
	}

	public static void setActionContext(ActionContext actionContext) {
		ACTION_CONTEXT.set(actionContext);
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static void setIocContext(IocContext iocContext) {
		IOC_CONTEXT.set(iocContext);
	}

	public static IocContext getIocContext() {
		return IOC_CONTEXT.get();
	}

	// ==================================================================

	// 重置当前线程所持有的对象
	public static void resetALL() {
		ACTION_CONTEXT.set(null);
		REQ.set(null);
		RESP.set(null);
		NAME.set(null);
		IOC_CONTEXT.set(null);
	}

	public static HttpSession getHttpSession() {
		return REQ.get().getSession();
	}

	public static HttpSession getHttpSession(boolean createNew) {
		return REQ.get().getSession(createNew);
	}
}
