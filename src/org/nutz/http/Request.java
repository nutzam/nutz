package org.nutz.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.json.Json;

public class Request {

	public static enum METHOD {
		GET, POST, OPTIONS, PUT, DELETE, TRACE, CONNECT, MULTIPART
	}

	private static HashMap<String, Object> EMPTY = new HashMap<String, Object>();

	public static Request get(String url) {
		return create(url, METHOD.GET, new HashMap<String, Object>());
	}

	public static Request get(String url, Header header) {
		return Request.create(url, METHOD.GET, EMPTY, header);
	}

	public static Request create(String url, METHOD method) {
		return create(url, method, EMPTY);
	}

	@SuppressWarnings("unchecked")
	public static Request create(String url, METHOD method, String paramsAsJson, Header header) {
		return create(url, method, (Map<String, Object>) Json.fromJson(paramsAsJson), header);
	}

	@SuppressWarnings("unchecked")
	public static Request create(String url, METHOD method, String paramsAsJson) {
		return create(url, method, (Map<String, ?>) Json.fromJson(paramsAsJson));
	}

	public static Request create(String url, METHOD method, Map<String, ?> params) {
		return Request.create(url, method, params, Header.create());
	}

	public static Request create(String url, METHOD method, Map<String, ?> params, Header header) {
		return new Request().setMethod(method).setParams(params).setUrl(url).setHeader(header);
	}

	private Request() {}

	private String url;
	private METHOD method;
	private Header header;
	private Map<String, ?> params;

	public URL getUrl() {
		StringBuilder sb = new StringBuilder(url);
		try {
			if (this.isGet() && null != params && params.size() > 0) {
				sb.append(url.indexOf('?') > 0 ? '&' : '?');
				for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					sb.append(Http.encode(key)).append('=').append(Http.encode(params.get(key)));
					if (it.hasNext())
						sb.append('&');
				}
			}
			return new URL(sb.toString());
		} catch (Exception e) {
			throw new HttpException(sb.toString(), e);
		}
	}

	public Map<String, ?> getParams() {
		return params;
	}

	private Request setParams(Map<String, ?> params) {
		this.params = params;
		return this;
	}

	public Request setUrl(String url) {
		this.url = url;
		return this;
	}

	public METHOD getMethod() {
		return method;
	}

	public boolean isMultipart() {
		return METHOD.MULTIPART == method;
	}

	public boolean isGet() {
		return METHOD.GET == method;
	}

	public boolean isPost() {
		return METHOD.POST == method;
	}

	public Request setMethod(METHOD method) {
		this.method = method;
		return this;
	}

	public Header getHeader() {
		return header;
	}

	public Request setHeader(Header header) {
		this.header = header;
		return this;
	}

	public Request setCookie(Cookie cookie) {
		header.set("cookie", cookie.toString());
		return this;
	}

	public Cookie getCookie() {
		String s = header.get("cookie");
		if (null == s)
			return new Cookie();
		return new Cookie(s);
	}

}
