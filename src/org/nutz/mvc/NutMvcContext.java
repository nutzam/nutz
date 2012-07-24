package org.nutz.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocContext;
import org.nutz.lang.util.SimpleContext;
import org.nutz.mvc.config.AtMap;

public class NutMvcContext extends SimpleContext {

    public Map<String, HttpServletRequest> reqs = new HashMap<String, HttpServletRequest>();
    public Map<String, HttpServletResponse> resps = new HashMap<String, HttpServletResponse>();
    public Map<String, ActionContext> actionCtxs = new HashMap<String, ActionContext>();
    public Map<String, IocContext> iocCtxs = new HashMap<String, IocContext>();
    public Map<String, Ioc> iocs = new HashMap<String, Ioc>();
    public Map<String, AtMap> atMaps = new HashMap<String, AtMap>();
    public Map<String, NutConfig> nutConfigs = new HashMap<String, NutConfig>();
    public Map<String, Map<String, Map<String, Object>>> localizations = new HashMap<String, Map<String, Map<String, Object>>>();
    
    public void close() {
        reqs.clear();
        resps.clear();
        actionCtxs.clear();
        iocCtxs.clear();
        iocs.clear();
        atMaps.clear();
        nutConfigs.clear();
        localizations.clear();
    }
}
