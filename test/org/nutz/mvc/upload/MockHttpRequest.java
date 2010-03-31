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

	@Override
	public int getContentLength() {
		return (int) mb.getContentLength();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new MockServletInputStream(mb);
	}

	@Override
	public String getContentType() {
		return mb.getContentType();
	}

	@Override
	public String getQueryString() {
		return null;
	}

	public MultipartBody getMb() {
		return mb;
	}

	public void setMb(MultipartBody mb) {
		this.mb = mb;
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getContextPath() {

		return null;
	}

	@Override
	public Cookie[] getCookies() {

		return null;
	}

	@Override
	public long getDateHeader(String arg0) {

		return 0;
	}

	@Override
	public String getHeader(String arg0) {

		return null;
	}

	@Override
	public Enumeration getHeaderNames() {

		return null;
	}

	@Override
	public Enumeration getHeaders(String arg0) {

		return null;
	}

	@Override
	public int getIntHeader(String arg0) {

		return 0;
	}

	@Override
	public String getMethod() {

		return null;
	}

	@Override
	public String getPathInfo() {

		return null;
	}

	@Override
	public String getPathTranslated() {

		return null;
	}

	@Override
	public String getRemoteUser() {

		return null;
	}

	@Override
	public String getRequestURI() {

		return null;
	}

	@Override
	public StringBuffer getRequestURL() {

		return null;
	}

	@Override
	public String getRequestedSessionId() {

		return null;
	}

	@Override
	public String getServletPath() {

		return null;
	}

	@Override
	public HttpSession getSession() {
		return new MockHttpSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {

		return null;
	}

	@Override
	public Principal getUserPrincipal() {

		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {

		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {

		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {

		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {

		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {

		return false;
	}

	@Override
	public Object getAttribute(String arg0) {

		return null;
	}

	@Override
	public Enumeration getAttributeNames() {

		return null;
	}

	@Override
	public String getCharacterEncoding() {

		return null;
	}

	@Override
	public String getLocalAddr() {

		return null;
	}

	@Override
	public String getLocalName() {

		return null;
	}

	@Override
	public int getLocalPort() {

		return 0;
	}

	@Override
	public Locale getLocale() {

		return null;
	}

	@Override
	public Enumeration getLocales() {

		return null;
	}

	@Override
	public String getParameter(String arg0) {

		return null;
	}

	@Override
	public Map getParameterMap() {

		return null;
	}

	@Override
	public Enumeration getParameterNames() {

		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {

		return null;
	}

	@Override
	public String getProtocol() {

		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {

		return null;
	}

	@Override
	public String getRealPath(String arg0) {

		return null;
	}

	@Override
	public String getRemoteAddr() {

		return null;
	}

	@Override
	public String getRemoteHost() {

		return null;
	}

	@Override
	public int getRemotePort() {

		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {

		return null;
	}

	@Override
	public String getScheme() {

		return null;
	}

	@Override
	public String getServerName() {

		return null;
	}

	@Override
	public int getServerPort() {

		return 0;
	}

	@Override
	public boolean isSecure() {

		return false;
	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {

	}

}
