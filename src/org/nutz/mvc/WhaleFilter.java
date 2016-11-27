package org.nutz.mvc;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.config.FilterNutConfig;

public class WhaleFilter implements Filter {

    protected FilterNutConfig config;
    protected String inputEnc;
    protected String outputEnc;
    protected String methodParam;
    protected boolean allowHTTPMethodOverride;
    private static WhaleFilter _me;

    public static WhaleFilter me() {
        return _me;
    }

    public void init(FilterConfig c) throws ServletException {
        _me = this;
        config = new FilterNutConfig(c);
        inputEnc = config.getInitParameter("inputEnc");
        outputEnc = config.getInitParameter("outputEnc");
        methodParam = config.getInitParameter("methodParam");
        allowHTTPMethodOverride = "true".equals(config.getInitParameter("allowHTTPMethodOverride"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;

        // 设置req的编码
        if (inputEnc != null)
            req.setCharacterEncoding(inputEnc);
        // 设置resp的编码
        if (outputEnc != null)
            resp.setCharacterEncoding(outputEnc);

        // 处理隐藏HTTP METHOD, _method参数模式
        if (methodParam != null && "POST".equals(req.getMethod())) {
            String qs = req.getQueryString();
            if (qs != null && qs.contains("_method=")) {
                final NutMap map = Mvcs.toParamMap(new StringReader(qs), inputEnc == null ? Charset.defaultCharset().name() : inputEnc);
                request = new HttpServletRequestWrapper(req) {
                    public String getMethod() {
                        return map.getString(methodParam);
                    }
                };
            }
        }
        // 处理 X-HTTP-Method-Override
        else if (allowHTTPMethodOverride && req.getHeader("X-HTTP-Method-Override") != null) {
            request = new HttpServletRequestWrapper(req) {
                public String getMethod() {
                    return req.getHeader("X-HTTP-Method-Override");
                }
            };
        }

        chain.doFilter(request, response);
    }

    public void destroy() {}
}
