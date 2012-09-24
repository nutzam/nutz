package org.nutz.mvc;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;
import org.nutz.mvc.config.AtMap;

public class NutMvcContext extends SimpleContext {

    public ThreadLocal<Context> reqThreadLocal = new ThreadLocal<Context>() {

        protected Context initialValue() {
            return Lang.context();
        }
    };
    public Map<String, Ioc> iocs = new HashMap<String, Ioc>();
    public Map<String, AtMap> atMaps = new HashMap<String, AtMap>();
    public Map<String, NutConfig> nutConfigs = new HashMap<String, NutConfig>();
    public Map<String, Map<String, Map<String, Object>>> localizations = new HashMap<String, Map<String, Map<String, Object>>>();
    
    public void close() {
        reqThreadLocal.set(Lang.context());
        iocs.clear();
        atMaps.clear();
        nutConfigs.clear();
        localizations.clear();
    }
    
    /**
     * 获取默认Ioc,在单个NutFilter/NutServlet中非常合用
     */
    public Ioc getDefaultIoc() {
        if (iocs.isEmpty())
            return null;
        return iocs.values().iterator().next();
    }
    
    public NutConfig getDefaultNutConfig() {
        if (nutConfigs.isEmpty())
            return null;
        return nutConfigs.values().iterator().next();
    }
}
