package org.nutz.mvc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.mvc.config.FilterNutConfig;

/**
 * 同 JSP/Serlvet 容器的挂接点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class NutFilter implements Filter {

    protected ActionHandler handler;

    private static final String IGNORE = "^.+\\.(jsp|png|gif|jpg|js|css|jspx|jpeg|swf|ico)$";

    private Pattern ignorePtn;

    private boolean skipMode;

    private String selfName;

    private SessionProvider sp;

    private boolean needRealName = true;

    public void init(FilterConfig conf) throws ServletException {
        Mvcs.setServletContext(conf.getServletContext());
        this.selfName = conf.getFilterName();
        Mvcs.set(selfName, null, null);

        FilterNutConfig config = new FilterNutConfig(conf);
        Mvcs.setNutConfig(config);
        // 如果仅仅是用来更新 Message 字符串的，不加载 Nutz.Mvc 设定
        // @see Issue 301
        String skipMode = Strings.sNull(conf.getInitParameter("skip-mode"), "false").toLowerCase();
        if (!"true".equals(skipMode)) {
            handler = new ActionHandler(config);
            String regx = Strings.sNull(config.getInitParameter("ignore"), IGNORE);
            if (!"null".equalsIgnoreCase(regx)) {
                ignorePtn = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
            }
        } else
            this.skipMode = true;
        sp = config.getSessionProvider();
    }

    public void destroy() {
        Mvcs.resetALL();
        Mvcs.set(selfName, null, null);
        if (handler != null)
            handler.depose();
        Mvcs.setServletContext(null);
        Mvcs.close();
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        String preName = Mvcs.getName();
        Context preContext = Mvcs.resetALL();
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        try {
            if (sp != null)
                req = sp.filter(request,
                                response,
                                Mvcs.getServletContext());
            if (needRealName && skipMode) {
                // 直接无视自己的名字!!到容器取nutzservlet的名字!!
                Enumeration<String> names = Mvcs.getServletContext().getAttributeNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    if (name.endsWith("_localization")) {
                        this.selfName = name.substring(0, name.length() - "_localization".length());
                        break;
                    }
                }
                needRealName = false;
            }
            Mvcs.set(this.selfName, request, response);
            if (!skipMode) {
                RequestPath path = Mvcs.getRequestPathObject(request);
                if (null == ignorePtn || !ignorePtn.matcher(path.getUrl()).find()) {
                    if (handler.handle(request, response))
                        return;
                }
            }
            // 更新 Request 必要的属性
            Mvcs.updateRequestAttributes((HttpServletRequest) req);
            // 本过滤器没有找到入口函数，继续其他的过滤器
            chain.doFilter(req, resp);
        }
        finally {
            Mvcs.resetALL();
            //仅当forward/incule时,才需要恢复之前设置
            if (null != (request.getAttribute("javax.servlet.forward.request_uri"))) {
                if (preName != null)
                    Mvcs.set(preName, request, response);
                if (preContext != null)
                    Mvcs.ctx.reqThreadLocal.set(preContext);
            }
        }
    }
}
