package org.nutz.http.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;

import org.nutz.lang.Lang;

public class NutHttpReq extends AbstractHttpObject2 {

	// ----------------------------------------------------------------
	// InputStream相关
	protected ServletInputStream in;
	private boolean canGetInputStream;

	public ServletInputStream getInputStream() throws IOException {
		if (canGetInputStream) {
			canGetInputStream = false;
			return in;
		}
		throw new IllegalStateException();
	}

	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream(),
				characterEncoding));
	}

	// ----------------------------------------------------------------
	// 请求参数
	protected Map<String, String[]> params = new HashMap<String, String[]>();

	public String getParameter(String name) {
		String[] obj = params.get(name);
		if (obj == null || obj.length < 1)
			return null;
		return obj[0];
	}

	public Map<String, String[]> getParameterMap() {
		return params;
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	public String[] getParameterValues(String name) {
		return params.get(name);
	}

	protected String queryString;

	public String getQueryString() {
		return queryString;
	}

	protected String pathInfo;

	public String getPathInfo() {
		return pathInfo;
	}

	public String getPathTranslated() {
		throw Lang.noImplement();
	}

	public String getProtocol() {
		return "HTTP/1.1";
	}

	public String getScheme() {
		return "http";
	}

	protected String requestURI;

	public String getRequestURI() {
		return requestURI;
	}

	protected String requestURL;

	public StringBuffer getRequestURL() {
		return new StringBuffer(requestURL);
	}

	// ------------------------------------
	protected String method;

	public String getMethod() {
		return method;
	}

	protected Cookie[] cookies;

	public Cookie[] getCookies() {
		return cookies;
	}

	// ----------------------------------------------------------------
	// 请求的元数据
	protected String characterEncoding = "UTF-8";

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
}
