package org.nutz.mvc.ioc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;

import jakarta.servlet.http.HttpSession;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class SessionIocContext implements IocContext {

    private HttpSession session;

    public SessionIocContext(HttpSession session) {
        this.session = session;
    }

    @Override
    public void clear() {
        synchronized (session) {
            Enumeration<String> ems = session.getAttributeNames();
            List<String> keys = new ArrayList<String>();
            while (ems.hasMoreElements()) {
                String key = ems.nextElement();
                if (null == key) {
                    continue;
                }
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

    @Override
    public void depose() {
        clear();
        session = null;
    }

    @Override
    public ObjectProxy fetch(String name) {
        Object re = session.getAttribute(name);
        if (re == null) {
            return null;
        }
        if (re instanceof ObjectProxy) {
            return (ObjectProxy) re;
        }
        return new ObjectProxy().setObj(re);
    }

    @Override
    public boolean remove(String scope, String name) {
        if (null != scope && "session".equals(scope)) {
            session.removeAttribute(name);
            return true;
        }
        return false;
    }

    @Override
    public boolean save(String scope, String name, ObjectProxy obj) {
        if (null != scope && "session".equals(scope)) {
            session.setAttribute(name, obj);
            return true;
        }
        return false;
    }

    public HttpSession getSession() {
        return session;
    }

    @Override
    public Set<String> names() {
        Set<String> list = new HashSet<String>();
        synchronized (session) {
            Enumeration<String> ems = session.getAttributeNames();
            while (ems.hasMoreElements()) {
                String key = ems.nextElement();
                if (null == key) {
                    continue;
                }
                Object value = session.getAttribute(key);
                if (value instanceof ObjectProxy) {
                    list.add(key);
                }
            }
        }
        return list;
    }
}
