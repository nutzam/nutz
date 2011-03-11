package org.nutz.mvc.config;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Loading;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.NutConfigException;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.impl.NutLoading;

public abstract class AbstractNutConfig implements NutConfig {

	private static final Log log = Logs.getLog(AbstractNutConfig.class);

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
			return by.value().newInstance();
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Context getLoadingContext() {
		return (Context) this.getServletContext().getAttribute(Loading.CONTEXT_NAME);
	}

	public String getAppRoot() {
		String root = getServletContext().getRealPath("/").replace('\\', '/');
		if (root.endsWith("/"))
			return root.substring(0, root.length() - 1);
		else if (root.endsWith("/."))
			return root.substring(0, root.length() - 2);
		return root;
	}

	public Ioc getIoc() {
		return Mvcs.getIoc(getServletContext());
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
		String name = Strings.trim(getInitParameter("modules"));
		try {
			Class<?> mainModule = null;
			if (!Strings.isBlank(name))
				mainModule = Lang.loadClass(name);
			
			if (null == mainModule) {
				throw new NutConfigException("You need declare modules parameter in your context configuration file!");
			} else if (log.isDebugEnabled())
				log.debugf("MainModule: <%s>", mainModule.getName());
			return mainModule;
		}
		catch (Exception e) {
			throw new NutConfigException(e);
		}
	}

	public AtMap getAtMap() {
		return this.getAttributeAs(AtMap.class, AtMap.class.getName());
	}

	protected List<String> enum2list(Enumeration<?> enums) {
		LinkedList<String> re = new LinkedList<String>();
		while (enums.hasMoreElements())
			re.add(enums.nextElement().toString());
		return re;
	}

}
