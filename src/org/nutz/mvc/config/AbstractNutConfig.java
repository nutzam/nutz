package org.nutz.mvc.config;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.nutz.Nutz;
import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.NutConfigException;
import org.nutz.mvc.SessionProvider;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.impl.ModuleProvider;
import org.nutz.mvc.loader.annotation.AnnotationModuleProvider;
import org.nutz.mvc.impl.NutLoading;
import org.nutz.resource.Scans;

public abstract class AbstractNutConfig implements NutConfig {

    private static final Log log = Logs.get();

    protected SessionProvider sessionProvider;

    protected Class<?> mainModule;

    public AbstractNutConfig(ServletContext context) {
        Scans.me().init(context);
        Json.clearEntityCache();
    }

    public Loading createLoading() {
        if (log.isInfoEnabled()) {
            log.infof("Nutz Version : %s ", Nutz.version());
            log.infof("Nutz.Mvc[%s] is initializing ...", getAppName());
        }
        if (log.isDebugEnabled()) {
            Properties sys = System.getProperties();
            log.debug("Web Container Information:");
            log.debugf(" - Default Charset : %s", Encoding.defaultEncoding());
            log.debugf(" - Current . path  : %s", new File(".").getAbsolutePath());
            log.debugf(" - Java Version    : %s", sys.get("java.version"));
            log.debugf(" - File separator  : %s", sys.get("file.separator"));
            log.debugf(" - Timezone        : %s", sys.get("user.timezone"));
            log.debugf(" - OS              : %s %s", sys.get("os.name"), sys.get("os.arch"));
            log.debugf(" - ServerInfo      : %s", getServletContext().getServerInfo());
            log.debugf(" - Servlet API     : %d.%d",
                    getServletContext().getMajorVersion(),
                    getServletContext().getMinorVersion());
            if (getServletContext().getMajorVersion() > 2
                    || getServletContext().getMinorVersion() > 4)
                log.debugf(" - ContextPath     : %s", getServletContext().getContextPath());
            log.debugf(" - context.tempdir : %s", getAttribute("javax.servlet.context.tempdir"));
            log.debugf(" - MainModule      : %s", getMainModule().getName());
        }
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
            if (new File("src/main/webapp").exists())
                return new File("src/main/webapp").getAbsolutePath();
            if (new File("src/main/resources/webapp").exists())
                return new File("src/main/resources/webapp").getAbsolutePath();
            return "./webapp";
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

    public String getMainModulePackage(){
        return getMainModule().getPackage().getName();
    }

    @Override
    public ModuleProvider getModuleProvider() {
        return new AnnotationModuleProvider(this, this.getMainModule());
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

	public void setMainModule(Class<?> mainModule) {
        this.mainModule = mainModule;
    }
}
