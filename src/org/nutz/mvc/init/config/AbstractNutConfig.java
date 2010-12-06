package org.nutz.mvc.init.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.init.AtMap;
import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc.init.NutConfigException;

public abstract class AbstractNutConfig implements NutConfig {

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
		if (Strings.isBlank(name))
			return null;
		try {
			return Lang.loadClass(name);
		}
		catch (Exception e) {
			throw new NutConfigException(e);
		}
	}

	public AtMap atMap() {
		return this.getAttributeAs(AtMap.class, AtMap.class.getName());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected List<String> enum2list(Enumeration enums) {
		return Lang.enum2collection(enums, new ArrayList<String>());
	}

}
