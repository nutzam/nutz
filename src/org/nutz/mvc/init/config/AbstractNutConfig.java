package org.nutz.mvc.init.config;

import javax.servlet.ServletContext;

import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc.init.NutConfigException;
import org.nutz.resource.ResourceScan;
import org.nutz.resource.Scans;

public abstract class AbstractNutConfig implements NutConfig {

	/**
	 * 如果在非 JSP/SERVLET 容器内，这个函数不保证返回正确的结果
	 * 
	 * @return 当前应用的上下文对象
	 */
	public abstract ServletContext getServletContext();

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
		if (Strings.isBlank(name))
			return null;
		try {
			return Class.forName(name);
		}
		catch (Exception e) {
			throw new NutConfigException(e);
		}
	}

	public ResourceScan scan() {
		return Scans.web(getServletContext());
	}

}
