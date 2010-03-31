package org.nutz.mvc.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SuppressWarnings("unchecked")
public class MockHttpRequest implements HttpServletRequest {

	private MultipartBody mb;

	public MockHttpRequest(MultipartBody mb) {
		this.mb = mb;
	}

	public int getContentLength() {
		return (int) mb.getContentLength();
	}

	public ServletInputStream getInputStream() throws IOException {
		return new MockServletInputStream(mb);
	}

	public String getContentType() {
		return mb.getContentType();
	}

	public String getQueryString() {
		return null;
	}

	public MultipartBody getMb() {
		return mb;
	}

	public void setMb(MultipartBody mb) {
		this.mb = mb;
	}

	public String getAuthType() {
		return null;
	}

	public String getContextPath() {

		return null;
	}

	public Cookie[] getCookies() {

		return null;
	}

	public long getDateHeader(String arg0) {

		return 0;
	}

	public String getHeader(String arg0) {

		return null;
	}

	public Enumeration getHeaderNames() {

		return null;
	}

	public Enumeration getHeaders(String arg0) {

		return null;
	}

	public int getIntHeader(String arg0) {

		return 0;
	}

	public String getMethod() {

		return null;
	}

	public String getPathInfo() {

		return null;
	}

	public String getPathTranslated() {

		return null;
	}

	public String getRemoteUser() {

		return null;
	}

	public String getRequestURI() {

		return null;
	}

	public StringBuffer getRequestURL() {

		return null;
	}

	public String getRequestedSessionId() {

		return null;
	}

	public String getServletPath() {

		return null;
	}

	public HttpSession getSession() {
		return new MockHttpSession();
	}

	public HttpSession getSession(boolean arg0) {

		return null;
	}

	public Principal getUserPrincipal() {

		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {

		return false;
	}

	public boolean isRequestedSessionIdFromURL() {

		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {

		return false;
	}

	public boolean isRequestedSessionIdValid() {

		return false;
	}

	public boolean isUserInRole(String arg0) {

		return false;
	}

	public Object getAttribute(String arg0) {

		return null;
	}

	public Enumeration getAttributeNames() {

		return null;
	}

	public String getCharacterEncoding() {

		return null;
	}

	public String getLocalAddr() {

		return null;
	}

	public String getLocalName() {

		return null;
	}

	public int getLocalPort() {

		return 0;
	}

	public Locale getLocale() {

		return null;
	}

	public Enumeration getLocales() {

		return null;
	}

	public String getParameter(String arg0) {

		return null;
	}

	public Map getParameterMap() {

		return null;
	}

	public Enumeration getParameterNames() {

		return null;
	}

	public String[] getParameterValues(String arg0) {

		return null;
	}

	public String getProtocol() {

		return null;
	}

	public BufferedReader getReader() throws IOException {

		return null;
	}

	public String getRealPath(String arg0) {

		return null;
	}

	public String getRemoteAddr() {

		return null;
	}

	public String getRemoteHost() {

		return null;
	}

	public int getRemotePort() {

		return 0;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {

		return null;
	}

	public String getScheme() {

		return null;
	}

	public String getServerName() {

		return null;
	}

	public int getServerPort() {

		return 0;
	}

	public boolean isSecure() {

		return false;
	}

	public void removeAttribute(String arg0) {

	}

	public void setAttribute(String arg0, Object arg1) {

	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {

	}

}
