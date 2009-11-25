package org.nutz.mvc.ioc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletRequest;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.weaver.StaticWeaver;

public class RequestIocContext implements IocContext {

	private ServletRequest req;

	public RequestIocContext(ServletRequest req) {
		this.req = req;
	}

	public void clear() {
		synchronized (req) {
            @SuppressWarnings("unchecked")
            Enumeration<String> ems = req.getAttributeNames();
            List<String> keys = new ArrayList<String>();
            while (ems.hasMoreElements()) {
                String key = ems.nextElement();
                if (null == key)
                    continue;
                Object value = req.getAttribute(key);
                if (value instanceof ObjectProxy) {
                    keys.add(key);
                    ((ObjectProxy) value).depose();
                }
            }
            for (String key : keys) {
                req.removeAttribute(key);
            }
        }
	}

	public void depose() {
		clear();
		req = null;
	}

	public ObjectProxy fetch(String name) {
		Object re = req.getAttribute(name);
		if (re == null)
			return null;
		if (re instanceof ObjectProxy)
			return (ObjectProxy) re;
		ObjectProxy op = new ObjectProxy();
		op.setWeaver(new StaticWeaver(re, null));
		return op;
	}

	public boolean remove(String scope, String name) {
		if (null != scope && "request".equals(scope)) {
			req.removeAttribute(name);
			return true;
		}
		return false;
	}

	public boolean save(String scope, String name, ObjectProxy obj) {
		if (null != scope && "request".equals(scope)) {
			req.setAttribute(name, obj);
			return true;
		}
		return false;
	}

}
