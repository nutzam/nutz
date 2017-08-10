package org.nutz.mvc.filter;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.VoidView;

import javax.servlet.http.HttpServletResponse;

/**
 * 如果是OPTIONS请求，那么返回自定义的Access-Control-Allow-*头部
 */
public class CrossOriginFilter implements ActionFilter {

    private static final Log log = Logs.get();

    protected String origin;
    protected String methods;
    protected String headers;
    protected String credentials;

    public CrossOriginFilter() {
        this("*", "get, post, put, delete, options", "origin, content-type, accept", "true");
    }

    public CrossOriginFilter(String origin, String methods, String headers, String credentials) {
        this.origin = origin;
        this.methods = methods;
        this.headers = headers;
        this.credentials = credentials;
    }

    public View match(ActionContext ac) {
        HttpServletResponse resp = ac.getResponse();
        if (!Strings.isBlank(origin))
            resp.setHeader("Access-Control-Allow-Origin", origin);
        if (!Strings.isBlank(methods))
            resp.setHeader("Access-Control-Allow-Methods", methods);
        if (!Strings.isBlank(headers))
            resp.setHeader("Access-Control-Allow-Headers", headers);
        if (!Strings.isBlank(credentials))
            resp.setHeader("Access-Control-Allow-Credentials", credentials);
        
        if ("OPTIONS".equals(ac.getRequest().getMethod())) {
            if (log.isDebugEnabled())
                log.debugf("Feedback -- [%s] [%s] [%s] [%s]", origin, methods, headers, credentials);
            return new VoidView();
        }
        return null;
    }
}
