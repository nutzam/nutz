package org.nutz.mock.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.multipart.MultipartInputStream;
import org.nutz.mvc.Mvcs;

public class MockHttpServletRequest extends HttpServletRequestWrapper {

    protected HttpSession session;

    protected String contextPath;

    protected String[] dispatcherTarget;

    public MockHttpServletRequest() {
        super(Mock.EmtryHttpServletRequest);
        this.headers = new HashMap<String, String>();
        this.dispatcherTarget = new String[1];
        Mvcs.set("", this, null);
    }

    public String getDispatcherTarget() {
        return this.dispatcherTarget[0];
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    protected Map<String, String> headers;

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void setHeader(String name, Object value) {
        headers.put(name, value.toString());
    }

    public Enumeration<String> getHeaderNames() {
        return Lang.enumeration(headers.keySet());
    }

    protected String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    protected String pathInfo;

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    protected String pathTranslated;

    public String getPathTranslated() {
        return pathTranslated;
    }

    public void setPathTranslated(String pathTranslated) {
        this.pathTranslated = pathTranslated;
    }

    // protected String queryString;

    public String getQueryString() {
        if (params.size() == 0)
            return null;
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getValue() == null)
                sb.append(entry.getKey()).append("=&");
            else
                for (String str : entry.getValue()) {
                    sb.append(entry.getKey()).append("=").append(str).append("&");
                }
        }
        return sb.toString();
    }

    // public void setQueryString(String queryString) {
    // this.queryString = queryString;
    // }

    public String remoteUser;

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    protected String requestURI;

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    protected StringBuffer requestURL;

    public StringBuffer getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(StringBuffer requestURL) {
        this.requestURL = requestURL;
    }

    public String getRequestedSessionId() {
        if (session != null)
            return session.getId();
        return null;
    }

    protected String servletPath;

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public HttpSession getSession(boolean flag) {
        return session;
    }

    public MockHttpServletRequest setSession(HttpSession session) {
        this.session = session;
        return this;
    }

    protected Principal userPrincipal;

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    protected Map<String, Object> attributeMap = new HashMap<String, Object>();

    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributeMap.keySet()).elements();
    }

    protected String characterEncoding;

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public int getContentLength() {
        String cl = this.getHeader("content-length");
        try {
            return Integer.parseInt(cl);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getContentType() {
        return this.getHeader("content-type");
    }

    protected ServletInputStream inputStream;

    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }

    public MockHttpServletRequest setInputStream(ServletInputStream ins) {
        this.inputStream = ins;
        return this;
    }

    public MockHttpServletRequest init() {
        if (null != inputStream)
            if (inputStream instanceof MultipartInputStream) {
                ((MultipartInputStream) inputStream).init();
                this.setCharacterEncoding(((MultipartInputStream) inputStream).getCharset());
                try {
                    this.setHeader("content-length", inputStream.available());
                    this.setHeader("content-type",
                                   ((MultipartInputStream) inputStream).getContentType());
                }
                catch (IOException e) {
                    throw Lang.wrapThrow(e);
                }
            }
        Mvcs.set("", this, null);
        return this;
    }

    protected Map<String, String[]> params = new HashMap<String, String[]>();

    public String getParameter(String key) {
        if (params.containsKey(key)) {
            return params.get(key)[0];
        }
        return null;
    }

    public void setParameter(String key, String value) {
        params.put(key, new String[]{value});
    }

    public void setParameter(String key, Number num) {
        setParameter(key, num.toString());
    }

    public void setParameterValues(String key, String[] values) {
        params.put(key, values);
    }

    public void addParameter(String key, String value) {
        params.put(key, new String[]{value});
    }

    public Map<String, String[]> getParameterMap() {
        return params;
    }

    public Enumeration<String> getParameterNames() {
        return new Vector<String>(params.keySet()).elements();
    }

    public String[] getParameterValues(String name) {
        Object param = params.get(name);
        return Castors.me().castTo(param, String[].class);
    }

    protected String protocol;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public RequestDispatcher getRequestDispatcher(String dest) {
        return new MockRequestDispatcher(dispatcherTarget, dest);
    }

    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }
}
