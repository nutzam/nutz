package org.nutz.mvc.ioc;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.weaver.StaticWeaver;

public class SessionIocContext implements IocContext {

	private HttpSession session;

	public SessionIocContext(HttpSession session) {
		this.session = session;
	}

	public void clear() {
		Enumeration<?> ems = session.getAttributeNames();
		while (ems.hasMoreElements()) {
			Object key = ems.nextElement();
			if (null == key)
				continue;
			Object value = session.getAttribute((String) key);
			if (null != value)
				if (value instanceof ObjectProxy)
					((ObjectProxy) value).depose();
			session.removeAttribute((String) key);
		}

	}

	public void depose() {
		clear();
		session = null;
	}

	public ObjectProxy fetch(String name) {
		Object re = session.getAttribute(name);
		if (re == null)
			return null;
		if (re instanceof ObjectProxy)
			return (ObjectProxy) re;
		ObjectProxy op = new ObjectProxy();
		op.setWeaver(new StaticWeaver(re, null));
		return op;
	}

	public boolean remove(String scope, String name) {
		if (null != scope && "session".equals(scope)) {
			session.removeAttribute(name);
			return true;
		}
		return false;
	}

	public boolean save(String scope, String name, ObjectProxy obj) {
		if (null != scope && "session".equals(scope)) {
			session.setAttribute(name, obj);
			return true;
		}
		return false;
	}

}
