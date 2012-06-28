package org.nutz.mock.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * 模拟ServletConfig
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MockServletConfig implements ServletConfig {
    
    private Map<String, String> initParameterMap = new HashMap<String, String>();
    
    private ServletContext servletContext;
    
    private String servletName;

    public MockServletConfig(MockServletContext servletContext, String string) {
        this.servletContext = servletContext;
        this.servletName = string;
    }

    public String getInitParameter(String key) {
        return initParameterMap.get(key);
    }

    public Enumeration<String> getInitParameterNames() {
        return new Vector<String>(initParameterMap.keySet()).elements();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getServletName() {
        return servletName;
    }

    public void addInitParameter(String key,String value){
        initParameterMap.put(key, value);
    }
    
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    public void setServletName(String servletName) {
        this.servletName = servletName;
    }
}
