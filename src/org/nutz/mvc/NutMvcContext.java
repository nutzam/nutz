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
    public Ioc ioc;
    public AtMap atMap;
    public NutConfig nutConfig;
    public Map<String, Map<String, Object>> localizations = new HashMap<String, Map<String, Object>>();

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
        ioc = null;
        atMap = null;
        nutConfig = null;
        localizations = null;
    }

    /**
     * 获取默认Ioc,在单个NutFilter/NutServlet中非常合用
     */
    public Ioc getDefaultIoc() {
        return ioc;
    }

    public NutConfig getDefaultNutConfig() {
        return nutConfig;
    }

    public Ioc getIoc() {
        return ioc;
    }

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    public AtMap getAtMap() {
        return atMap;
    }

    public void setAtMap(AtMap atMap) {
        this.atMap = atMap;
    }

    public NutConfig getNutConfig() {
        return nutConfig;
    }

    public void setNutConfig(NutConfig nutConfig) {
        this.nutConfig = nutConfig;
    }

    public Map<String, Map<String, Object>> getLocalizations() {
        return localizations;
    }

    public void setLocalizations(Map<String, Map<String, Object>> localizations) {
        this.localizations = localizations;
    }
}
