package org.nutz.mock.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.lang.Lang;
import org.nutz.mock.servlet.multipart.MultipartInputStream;

public class MockHttpServletRequest implements HttpServletRequest {

	protected HttpSession session;
	
	protected String contextPath;

	public MockHttpServletRequest() {
		this.headers = new HashMap<String, String>();
	}

	public String getAuthType() {
		throw Lang.noImplement();
	}

	public String getContextPath() {
		return contextPath;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public Cookie[] getCookies() {
		throw Lang.noImplement();
	}

	public long getDateHeader(String arg0) {
		throw Lang.noImplement();
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

	public Enumeration<?> getHeaders(String name) {
		throw Lang.noImplement();
	}

	public int getIntHeader(String arg0) {
		throw Lang.noImplement();
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

	protected String queryString;

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

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

	public boolean isRequestedSessionIdFromCookie() {
		throw Lang.noImplement();
	}

	public boolean isRequestedSessionIdFromURL() {
		throw Lang.noImplement();
	}

	public boolean isRequestedSessionIdFromUrl() {
		throw Lang.noImplement();
	}

	public boolean isRequestedSessionIdValid() {
		throw Lang.noImplement();
	}

	public boolean isUserInRole(String arg0) {
		throw Lang.noImplement();
	}

	protected Map<String, Object> attributeMap = new HashMap<String, Object>();

	public Object getAttribute(String key) {
		return attributeMap.get(key);
	}

	public Enumeration<Object> getAttributeNames() {
		return new Vector<Object>(attributeMap.keySet()).elements();
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
				try {
					this.setHeader("content-length", inputStream.available());
					this.setHeader(	"content-type",
									((MultipartInputStream) inputStream).getContentType());
				}
				catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
		return this;
	}

	public String getLocalAddr() {
		throw Lang.noImplement();
	}

	public String getLocalName() {
		throw Lang.noImplement();
	}

	public int getLocalPort() {
		throw Lang.noImplement();
	}

	public Locale getLocale() {
		throw Lang.noImplement();
	}

	public Enumeration<Locale> getLocales() {
		throw Lang.noImplement();
	}

	protected Map<String, String> parameterMap = new HashMap<String, String>();

	public String getParameter(String key) {
		return parameterMap.get(key);
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public Enumeration<String> getParameterNames() {
		return new Vector<String>(parameterMap.keySet()).elements();
	}

	public String[] getParameterValues(String arg0) {
		return parameterMap.values().toArray(new String[0]);
	}

	protected String protocol;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public BufferedReader getReader() throws IOException {
		throw Lang.noImplement();
	}

	public String getRealPath(String arg0) {
		throw Lang.noImplement();
	}

	public String getRemoteAddr() {
		throw Lang.noImplement();
	}

	public String getRemoteHost() {
		throw Lang.noImplement();
	}

	public int getRemotePort() {
		throw Lang.noImplement();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw Lang.noImplement();
	}

	public String getScheme() {
		throw Lang.noImplement();
	}

	public String getServerName() {
		throw Lang.noImplement();
	}

	public int getServerPort() {
		throw Lang.noImplement();
	}

	public boolean isSecure() {
		throw Lang.noImplement();
	}

	public void removeAttribute(String key) {
		attributeMap.remove(key);
	}

	public void setAttribute(String key, Object value) {
		attributeMap.put(key, value);
	}

	public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
		this.characterEncoding = characterEncoding;
	}

}
