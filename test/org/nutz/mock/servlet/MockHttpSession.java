package org.nutz.mock.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.nutz.lang.Lang;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class MockHttpSession implements HttpSession {

    protected ServletContext servletContext;

    public MockHttpSession(MockServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected Map<String, Object> attributeMap = new HashMap<String, Object>();

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public long getCreationTime() {
        throw Lang.noImplement();
    }

    @Override
    public String getId() {
        throw Lang.noImplement();
    }

    @Override
    public long getLastAccessedTime() {
        throw Lang.noImplement();
    }

    @Override
    public int getMaxInactiveInterval() {
        throw Lang.noImplement();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    public Object getValue(String arg0) {
        throw Lang.noImplement();
    }

    public String[] getValueNames() {
        throw Lang.noImplement();
    }

    @Override
    public void invalidate() {
        throw Lang.noImplement();
    }

    @Override
    public boolean isNew() {
        throw Lang.noImplement();
    }

    public void putValue(String arg0, Object arg1) {
        throw Lang.noImplement();
    }

    public void removeValue(String arg0) {
        throw Lang.noImplement();
    }

    @Override
    public void setMaxInactiveInterval(int arg0) {
        throw Lang.noImplement();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributeMap.keySet()).elements();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

}
