package org.nutz.mock.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.multipart.MultipartInputStream;
import org.nutz.mvc.Mvcs;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

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

    @Override
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    protected Map<String, String> headers;

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    public void setHeader(String name, Object value) {
        headers.put(name, value.toString());
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Lang.enumeration(headers.keySet());
    }

    protected String method;

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    protected String pathInfo;

    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    protected String pathTranslated;

    @Override
    public String getPathTranslated() {
        return pathTranslated;
    }

    public void setPathTranslated(String pathTranslated) {
        this.pathTranslated = pathTranslated;
    }

    // protected String queryString;

    @Override
    public String getQueryString() {
        if (params.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                sb.append(entry.getKey()).append("=&");
            } else {
                for (String str : entry.getValue()) {
                    sb.append(entry.getKey()).append("=").append(str).append("&");
                }
            }
        }
        return sb.toString();
    }

    // public void setQueryString(String queryString) {
    // this.queryString = queryString;
    // }

    public String remoteUser;

    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    protected String requestURI;

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    protected StringBuffer requestURL;

    @Override
    public StringBuffer getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(StringBuffer requestURL) {
        this.requestURL = requestURL;
    }

    @Override
    public String getRequestedSessionId() {
        if (session != null) {
            return session.getId();
        }
        return null;
    }

    protected String servletPath;

    @Override
    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean flag) {
        return session;
    }

    public MockHttpServletRequest setSession(HttpSession session) {
        this.session = session;
        return this;
    }

    protected Principal userPrincipal;

    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    protected Map<String, Object> attributeMap = new HashMap<String, Object>();

    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributeMap.keySet()).elements();
    }

    protected String characterEncoding;

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public int getContentLength() {
        String cl = this.getHeader("content-length");
        try {
            return Integer.parseInt(cl);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String getContentType() {
        return this.getHeader("content-type");
    }

    protected ServletInputStream inputStream;

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }

    public MockHttpServletRequest setInputStream(ServletInputStream ins) {
        this.inputStream = ins;
        return this;
    }

    public MockHttpServletRequest init() {
        if (null != inputStream) {
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
        }
        Mvcs.set("", this, null);
        return this;
    }

    protected Map<String, String[]> params = new HashMap<String, String[]>();

    @Override
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

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector<String>(params.keySet()).elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        Object param = params.get(name);
        return Castors.me().castTo(param, String[].class);
    }

    protected String protocol;

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String dest) {
        return new MockRequestDispatcher(dispatcherTarget, dest);
    }

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }
}
