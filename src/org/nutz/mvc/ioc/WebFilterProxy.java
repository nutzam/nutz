package org.nutz.mvc.ioc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;

public class WebFilterProxy implements Filter {
    
    protected String beanName;
    
    protected Filter proxy;
    
    protected FilterConfig filterConfig;
    
    protected Object lock = new Object();

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.beanName = filterConfig.getInitParameter("beanName");
        if (Strings.isBlank(beanName))
            beanName = filterConfig.getFilterName();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (proxy == null) {
            synchronized (lock) {
                if (proxy == null) {
                    Ioc ioc = Mvcs.ctx().getDefaultIoc();
                    Filter proxy = ioc.get(null, beanName);
                    proxy.init(filterConfig);
                    this.proxy = proxy;
                }
            }
        }
        proxy.doFilter(request, response, chain);
    }

    public void destroy() {
        if (proxy != null)
            proxy.destroy();
    }

}
