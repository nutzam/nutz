package org.nutz.mvc;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;
import org.nutz.mvc.config.AtMap;

public class NutMvcContext extends SimpleContext {

    private ThreadLocal<Context> reqThreadLocal = new ThreadLocal<Context>();
    public Map<String, Ioc> iocs = new HashMap<String, Ioc>();
    public Map<String, AtMap> atMaps = new HashMap<String, AtMap>();
    public Map<String, NutConfig> nutConfigs = new HashMap<String, NutConfig>();
    public Map<String, Map<String, Map<String, Object>>> localizations = new HashMap<String, Map<String, Map<String, Object>>>();
    
    public Context reqCtx() {
    	Context ctx = reqThreadLocal.get();
    	if (ctx == null) {
    		ctx = Lang.context();
    		reqThreadLocal.set(ctx);
    	}
    	return ctx;
    }
    
    public void reqCtx(Context ctx) {
    	reqThreadLocal.set(ctx);
    }
    
    public void removeReqCtx() {
    	reqThreadLocal.remove();
    }
    
    public void close() {
        reqThreadLocal.remove();
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
