package org.nutz.mock.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpSession;

import org.nutz.http.server.impl.servlet.NutHttpServletRequest;
import org.nutz.lang.Lang;
import org.nutz.mock.servlet.multipart.MultipartInputStream;
import org.nutz.mvc.Mvcs;

public class MockHttpServletRequest extends NutHttpServletRequest {

	protected HttpSession session;

	protected String contextPath;

	protected String[] dispatcherTarget;

	public MockHttpServletRequest() {
		this.headers = new HashMap<String, String>();
		this.dispatcherTarget = new String[1];
		requestURI = "/";
		contextPath = "";
		Mvcs.set("", this, null);
	}

	public String getDispatcherTarget() {
		return this.dispatcherTarget[0];
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public String getContextPath() {
		return contextPath;
	}

	public void setHeader(String name, Object value) {
		headers.put(name, value.toString());
	}
	
	public void setMethod(String method) {
		this.method = method;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

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

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}


	public void setRequestURL(StringBuffer requestURL) {
		this.requestURL = requestURL.toString();
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}
	public HttpSession getSession() {
		return session;
	}

	public MockHttpServletRequest setInputStream(ServletInputStream ins) {
		this.in = ins;
		return this;
	}

	public MockHttpServletRequest init() {
		if (null != in)
			if (in instanceof MultipartInputStream) {
				((MultipartInputStream) in).init();
				this.setCharacterEncoding(((MultipartInputStream) in).getCharset());
				try {
					this.setHeader("Content-Length", in.available());
					this.setHeader(	"Content-Type",
									((MultipartInputStream) in).getContentType());
				}
				catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
		Mvcs.set("", this, null);
		return this;
	}


	public void setParameter(String key, String value) {
		params.put(key, new String[]{value});
	}

	public RequestDispatcher getRequestDispatcher(String dest) {
		return new MockRequestDispatcher(dispatcherTarget, dest);
	}

	public void addParameter(String name, String value) {
		String[] strs = params.get(name);
		if (strs == null) {
			params.put(name, new String[]{value});
		} else {
			String[] vs = new String[strs.length + 1];
			for (int i = 0; i < strs.length; i++) {
				vs[i] = strs[i];
			}
			vs[strs.length] = value;
			params.put(name, vs);
		}
	}

	public void setParameterValues(String key, String[] array) {
		params.put(key, array);
	}

	public ServletInputStream getInputStream() throws IOException {
		return in;
	}
}
