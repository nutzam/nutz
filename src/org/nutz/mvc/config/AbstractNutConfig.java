package org.nutz.mvc.config;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.NutConfigException;
import org.nutz.mvc.SessionProvider;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.ViewMaker;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.impl.NutLoading;
import org.nutz.resource.Scans;

public abstract class AbstractNutConfig implements NutConfig {

    private static final Log log = Logs.get();
    
    protected SessionProvider sessionProvider;
    
    protected UrlMapping urlMapping;
    
    protected ActionChainMaker chainMaker;
    
    protected ViewMaker[] viewMakers;
    
    protected Class<?> mainModule;
    
    public AbstractNutConfig(ServletContext context) {
        Scans.me().init(context);
        Json.clearEntityCache();
    }

    public Loading createLoading() {
        /*
         * 确保用户声明了 MainModule
         */
        Class<?> mainModule = getMainModule();
        
        /*
         * 获取 Loading
         */
        LoadingBy by = mainModule.getAnnotation(LoadingBy.class);
        if (null == by) {
            if (log.isDebugEnabled())
                log.debug("Loading by " + NutLoading.class);
            return new NutLoading();
        }
        try {
            if (log.isDebugEnabled())
                log.debug("Loading by " + by.value());
            return Mirror.me(by.value()).born();
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    public Context getLoadingContext() {
        return (Context) this.getServletContext().getAttribute(Loading.CONTEXT_NAME);
    }

    public String getAppRoot() {
        String webinf = getServletContext().getRealPath("/WEB-INF/");
        if (webinf == null) {
            log.info("/WEB-INF/ not Found?!");
            return "";
        }
        String root = getServletContext().getRealPath("/").replace('\\', '/');
        if (root.endsWith("/"))
            return root.substring(0, root.length() - 1);
        else if (root.endsWith("/."))
            return root.substring(0, root.length() - 2);
        return root;
    }

    public Ioc getIoc() {
        return Mvcs.getIoc();
    }

    public Object getAttribute(String name) {
        return this.getServletContext().getAttribute(name);
    }

    public List<String> getAttributeNames() {
        return enum2list(this.getServletContext().getAttributeNames());
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttributeAs(Class<T> type, String name) {
        Object obj = getAttribute(name);
        if (null == obj)
            return null;
        if (type.isInstance(obj))
            return (T) obj;
        return Castors.me().castTo(obj, type);
    }

    public void setAttribute(String name, Object obj) {
        this.getServletContext().setAttribute(name, obj);
    }

    public void setAttributeIgnoreNull(String name, Object obj) {
        if (null != obj)
            setAttribute(name, obj);
    }

    public Class<?> getMainModule() {
        if (mainModule != null)
            return mainModule;
        String name = Strings.trim(getInitParameter("modules"));
        try {
            if (Strings.isBlank(name))
            	throw new NutConfigException("You need declare 'modules' parameter in your context configuration file or web.xml ! Only found -> " + getInitParameterNames());
            mainModule = Lang.loadClass(name);
            return mainModule;
        }
        catch (NutConfigException e) {
			throw e;
		}
        catch (Exception e) {
            throw new NutConfigException(e);
        }
    }

    public AtMap getAtMap() {
        return Mvcs.getAtMap();
    }

    protected List<String> enum2list(Enumeration<?> enums) {
        LinkedList<String> re = new LinkedList<String>();
        while (enums.hasMoreElements())
            re.add(enums.nextElement().toString());
        return re;
    }

    public void setSessionProvider(SessionProvider provider) {
        this.sessionProvider = provider;
    }
    
    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }

	public UrlMapping getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	public ActionChainMaker getActionChainMaker() {
		return chainMaker;
	}
	
	public void setActionChainMaker(ActionChainMaker acm) {
		this.chainMaker = acm;
	}
	
	public void setViewMakers(ViewMaker[] makers) {
		this.viewMakers = makers;
	}
	
	public ViewMaker[] getViewMakers() {
		return viewMakers;
	}
	
	public void setMainModule(Class<?> mainModule) {
        this.mainModule = mainModule;
    }
}
