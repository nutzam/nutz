package org.nutz.mock.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.nutz.lang.Lang;

@SuppressWarnings("deprecation")
public class MockHttpSession implements HttpSession {
    
    protected ServletContext servletContext;

    public MockHttpSession(MockServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected Map<String, Object> attributeMap = new HashMap<String, Object>();

    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    public long getCreationTime() {
        throw Lang.noImplement();
    }

    public String getId() {
        throw Lang.noImplement();
    }

    public long getLastAccessedTime() {
        throw Lang.noImplement();
    }

    public int getMaxInactiveInterval() {
        throw Lang.noImplement();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public Object getValue(String arg0) {
        throw Lang.noImplement();
    }

    public String[] getValueNames() {
        throw Lang.noImplement();
    }

    public void invalidate() {
        throw Lang.noImplement();
    }

    public boolean isNew() {
        throw Lang.noImplement();
    }

    public void putValue(String arg0, Object arg1) {
        throw Lang.noImplement();
    }

    public void removeValue(String arg0) {
        throw Lang.noImplement();
    }

    public void setMaxInactiveInterval(int arg0) {
        throw Lang.noImplement();
    }

    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributeMap.keySet()).elements();
    }

    /**
     * @deprecated
     */
    public HttpSessionContext getSessionContext() {
        return null;
    }

}
