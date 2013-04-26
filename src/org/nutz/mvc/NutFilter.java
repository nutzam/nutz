package org.nutz.mvc;

import java.io.IOException;
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

    private String selfName;

    private SessionProvider sp;

    private NutFilter2 proxyFilter;//代理老版本的Filter

    public void init(FilterConfig conf) throws ServletException {
    	if ("true".equals(Strings.sNull(conf.getInitParameter("skip-mode"), "false").toLowerCase())) {
    		proxyFilter = new NutFilter2();
    		return;
    	}
        Mvcs.setServletContext(conf.getServletContext());
        this.selfName = conf.getFilterName();
        Mvcs.set(selfName, null, null);

        FilterNutConfig config = new FilterNutConfig(conf);
        Mvcs.setNutConfig(config);
        handler = new ActionHandler(config);
        String regx = Strings.sNull(config.getInitParameter("ignore"), IGNORE);
        if (!"null".equalsIgnoreCase(regx)) {
            ignorePtn = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        }
        sp = config.getSessionProvider();
    }

    public void destroy() {
    	if (proxyFilter != null)
    		return;
        Mvcs.resetALL();
        Mvcs.set(selfName, null, null);
        if (handler != null)
            handler.depose();
        Mvcs.setServletContext(null);
        Mvcs.close();
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
    	if (proxyFilter != null) {
    		proxyFilter.doFilter(req, resp, chain);
    		return;
    	}
        String preName = Mvcs.getName();
        Context preContext = Mvcs.resetALL();
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        try {
            if (sp != null)
                req = sp.filter(request,
                                response,
                                Mvcs.getServletContext());
            Mvcs.set(this.selfName, request, response);
            RequestPath path = Mvcs.getRequestPathObject(request);
            if (null == ignorePtn || !ignorePtn.matcher(path.getUrl()).find()) {
                if (handler.handle(request, response))
                    return;
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
