package org.nutz.mvc.config;

import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.nutz.mvc.Mvcs;

public class FilterNutConfig extends AbstractNutConfig {

    private FilterConfig config;

    public FilterNutConfig(FilterConfig config) {
        super(config.getServletContext());
        this.config = config;
        Mvcs.setAtMap(new AtMap());
    }

    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    public String getInitParameter(String name) {
        return config.getInitParameter(name);
    }

    public List<String> getInitParameterNames() {
        return enum2list(config.getInitParameterNames());
    }

    public String getAppName() {
        return config.getFilterName();
    }

}
