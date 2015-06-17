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
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
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

    // TODO 这个变量应该在 1.b.46 之后的某一个版本删掉
    public static final String DEFAULT_MSGS = "$default";

    public static final String MSG = "msg";
    public static final String LOCALE_KEY = "nutz_mvc_localization_key";

    // ====================================================================

    public static Map<String, Object> getLocaleMessage(String key) {
        Map<String, Map<String, Object>> msgss = getMessageSet();
        if (null != msgss)
            return msgss.get(key);
        return null;
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
     * 获取当前会话的本地字符串集合的键值；如果当前 HTTP 会话不存在，则返回 null
     * 
     * @return 当前会话的本地字符串集合的键值；如果当前 HTTP 会话不存在，则返回 null
     */
    public static String getLocalizationKey() {
        HttpSession sess = getHttpSession(false);
        return null == sess ? null : (String) sess.getAttribute(LOCALE_KEY);
    }

    /**
     * 设置本地化字符串的键值
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
     * 返回当前加载了的本地化字符串的键值
     * 
     * @return 当前都加载了哪些种字符串的 KEY
     */
    public static Set<String> getLocalizationKeySet() {
        Map<String, Map<String, Object>> msgss = getMessageSet();
        if (null == msgss)
            return new HashSet<String>();
        return msgss.keySet();
    }

    /**
     * 默认的本地化字符串 KEY，当为 NULL 时，Nutz.Mvc 会随便用一个
     */
    private static String default_localization_key = null;

    /**
     * 设置默认的多国语言
     * 
     * @param key
     *            默认的多国语言 KEY
     */
    public static void setDefaultLocalizationKey(String key) {
        default_localization_key = key;
    }

    /**
     * 返回默认的本地化字符串 KEY
     * 
     * @return 默认的本地化字符串 KEY
     */
    public static String getDefaultLocalizationKey() {
        return default_localization_key;
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
        if (msgss == null && !ctx().localizations.isEmpty())
            msgss = ctx().localizations.values().iterator().next();
        if (null != msgss) {
            Map<String, Object> msgs = null;

            String lKey = Strings.sBlank(Mvcs.getLocalizationKey(), getDefaultLocalizationKey());

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
     * 
     * @param req
     *            HTTP 请求对象
     */
    public static String getRequestPath(HttpServletRequest req) {
        return getRequestPathObject(req).getPath();
    }

    /**
     * 获取当前请求的路径，并去掉后缀
     * 
     * @param req
     *            HTTP 请求对象
     */
    public static RequestPath getRequestPathObject(HttpServletRequest req) {
        String url = req.getPathInfo();
        if (null == url)
            url = req.getServletPath();
        return getRequestPathObject(url);
    }

    /**
     * 获取当前请求的路径，并去掉后缀
     * 
     * @param url
     *            请求路径的URL
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
        if (resp.getContentType() == null)
            resp.setContentType("text/plain");

        // by mawm 改为直接采用resp.getWriter()的方式直接输出!
        Json.toJson(resp.getWriter(), obj, format);

        resp.flushBuffer();
    }

    // ==================================================================
    private static final ThreadLocal<String> NAME = new ThreadLocal<String>();

    /**
     * NutMvc的上下文
     */
    @Deprecated
    public static NutMvcContext ctx;

    public static NutMvcContext ctx() {
        ServletContext sc = getServletContext();
        if (sc == null) {
            if (ctx == null)
                ctx = new NutMvcContext();
            return ctx;
        }
        NutMvcContext c = (NutMvcContext) getServletContext().getAttribute("__nutz__mvc__ctx");
        if (c == null) {
            c = new NutMvcContext();
            getServletContext().setAttribute("__nutz__mvc__ctx", c);
            ctx = c;
        }
        return c;
    }

    private static ServletContext def_servletContext;
    private static ThreadLocal<ServletContext> servletContext = new ThreadLocal<ServletContext>();

    /**
     * 获取 HTTP 请求对象
     * 
     * @return HTTP 请求对象
     */
    public static final HttpServletRequest getReq() {
        return reqt().getAs(HttpServletRequest.class, "req");
    }

    /**
     * 获取 HTTP 响应对象
     * 
     * @return HTTP 响应对象
     */
    public static final HttpServletResponse getResp() {
        return reqt().getAs(HttpServletResponse.class, "resp");
    }

    public static final String getName() {
        return NAME.get();
    }

    /**
     * 获取 Action 执行的上下文
     * 
     * @return Action 执行的上下文
     */
    public static final ActionContext getActionContext() {
        return reqt().getAs(ActionContext.class, "ActionContext");
    }

    public static void set(String name, HttpServletRequest req, HttpServletResponse resp) {
        NAME.set(name);
        reqt().set("req", req);
        reqt().set("resp", resp);
    }

    /**
     * 设置 Servlet 执行的上下文
     * 
     * @param servletContext
     *            Servlet 执行的上下文
     */
    public static void setServletContext(ServletContext servletContext) {
        if (def_servletContext == null)
            def_servletContext = servletContext;
        Mvcs.servletContext.set(servletContext);
    }

    /**
     * 设置 Action 执行的上下文
     * 
     * @param actionContext
     *            Action 执行的上下文
     */
    public static void setActionContext(ActionContext actionContext) {
        reqt().set("ActionContext", actionContext);
    }

    /**
     * 获取 Servlet 执行的上下文
     * 
     * @return Servlet 执行的上下文
     */
    public static ServletContext getServletContext() {
        ServletContext cnt = servletContext.get();
        if (cnt != null)
            return cnt;
        return def_servletContext;
    }

    /**
     * 设置对象装配的上下文环境
     * 
     * @param iocContext
     *            对象装配的上下文环境
     */
    public static void setIocContext(IocContext iocContext) {
        reqt().set("IocContext", iocContext);
    }

    /**
     * 获取对象装配的上下文环境
     * 
     * @return 进行对象装配的上下文环境
     */
    public static IocContext getIocContext() {
        return reqt().getAs(IocContext.class, "IocContext");
    }

    // 新的,基于ThreadLoacl改造过的Mvc辅助方法
    // ====================================================================

    /**
     * 获取全局的Ioc对象
     * 
     * @return 全局的Ioc对象
     */
    public static Ioc getIoc() {
        return ctx().iocs.get(getName());
    }

    public static void setIoc(Ioc ioc) {
        ctx().iocs.put(getName(), ioc);
    }

    public static AtMap getAtMap() {
        return ctx().atMaps.get(getName());
    }

    public static void setAtMap(AtMap atmap) {
        ctx().atMaps.put(getName(), atmap);
    }

    public static Map<String, Map<String, Object>> getMessageSet() {
        return ctx().localizations.get(getName());
    }

    public static void setMessageSet(Map<String, Map<String, Object>> messageSet) {
        ctx().localizations.put(getName(), messageSet);
    }

    public static void setNutConfig(NutConfig config) {
        ctx().nutConfigs.put(getName(), config);
    }

    public static NutConfig getNutConfig() {
        return ctx().nutConfigs.get(getName());
    }

    // ==================================================================

    /**
     * 重置当前线程所持有的对象
     */
    public static Context resetALL() {
        Context context = reqt();
        NAME.set(null);
        ctx().reqThreadLocal.set(Lang.context());
        return context;
    }

    public static HttpSession getHttpSession() {
        return getHttpSession(true);
    }

    public static HttpSession getHttpSession(boolean createNew) {
        HttpServletRequest req = getReq();
        if (null == req)
            return null;
        return req.getSession(createNew);
    }

    public static void close() {
        ctx().clear();
        ctx().close();
        ctx = new NutMvcContext();
    }
    
    public static Context reqt() {
        Context _reqt = ctx().reqThreadLocal.get();
        if (_reqt == null) {
            _reqt = Lang.context();
            ctx().reqThreadLocal.set(_reqt);
        }
        return _reqt;
    }

    /** 在入口方法调用时,是否禁用1.b.51新加入的FastClass功能, 默认禁用 */
    // PS: 如果这个修改导致异常,请报issue,并将这个变量设置为true
    public static boolean disableFastClassInvoker = true;
}
