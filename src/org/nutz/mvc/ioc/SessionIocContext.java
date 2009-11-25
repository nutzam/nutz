package org.nutz.mvc.ioc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
		synchronized (session) {
            @SuppressWarnings("unchecked")
            Enumeration<String> ems = session.getAttributeNames();
            List<String> keys = new ArrayList<String>();
            while (ems.hasMoreElements()) {
                String key = ems.nextElement();
                if (null == key)
                    continue;
                Object value = session.getAttribute(key);
                if (value instanceof ObjectProxy) {
                    keys.add(key);
                    ((ObjectProxy) value).depose();
                }
            }
            for (String key : keys) {
            	session.removeAttribute(key);
            }
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
