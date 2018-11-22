package org.nutz.mvc.config;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.nutz.mvc.Mvcs;

public class ServletNutConfig extends AbstractNutConfig {

    private ServletConfig config;

    public ServletNutConfig(ServletConfig config) {
        super(config.getServletContext());
        this.config = config;
        Mvcs.setAtMap(new AtMap());
    }

    @Override
    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    @Override
    public String getInitParameter(String name) {
        return config.getInitParameter(name);
    }

    @Override
    public List<String> getInitParameterNames() {
        return enum2list(config.getInitParameterNames());
    }

    @Override
    public String getAppName() {
        return config.getServletName();
    }

}
