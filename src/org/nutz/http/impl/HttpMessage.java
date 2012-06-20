package org.nutz.http.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.nutz.lang.Lang;

public abstract class HttpMessage extends HttpObject {
	//----------------------------------------------------------------
	//Header相关
	protected Map<String, List<String>> headers = new HashMap<String, List<String>>();
	public Map<String, List<String>> headers() {
		return headers;
	}
	
	public String getHeader(String name) {
		List<String> hs = headers.get(name);
		if (hs == null || hs.isEmpty())
			return null;
		return hs.get(0);
	}
	
	public long getDateHeader(String name) {
		String str = getHeader(name);
		if (str == null)
			return -1;
		try {
			return Https.httpData(str).getTime();
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public int getIntHeader(String name) {
		String str = getHeader(name);
		if (str == null)
			return -1;
		return Integer.parseInt(str);
	}
	
	public Set<String> getHeaderNames() {
		return headers.keySet();
	}
	
	public List<String> getHeaders(String name) {
		return headers.get(name);
	}
	public void addDateHeader(String key, long value) {
		addHeader(key, Https.httpDate(new Date(value)));
	}
	public void addHeader(String key, String value) {
		if (headers.containsKey(key))
			headers.get(key).add(value);
		else {
			List<String> hs = new ArrayList<String>();
			hs.add(value);
			headers.put(key, hs);
		}
	}
	public void addIntHeader(String key, int value) {
		addHeader(key, ""+value);
	}
	public boolean containsHeader(String key) {
		return headers.containsKey(key);
	}
	public void setDateHeader(String key, long value) {
		setHeader(key, Https.httpDate(new Date(value)));
	}
	public void setIntHeader(String key, int value) {
		setHeader(key, ""+value);
	}
	public void setHeader(String key, String value) {
		List<String> hs = headers.get(key);
		if (hs == null) {
			hs = new ArrayList<String>();
			hs.add(value);
			headers.put(key, hs);
		} else {
			hs.clear();
			hs.add(value);
		}
	}
	//----------------------------------
	
	public int getContentLength() {
		return getIntHeader("Content-Length");
	}

	public void setContentLength(int len) {
		setIntHeader("Content-Length", len);
	}
	public void setContentType(String type) {
		setHeader("Content-Type", type);
	}
	public String getContentType() {
		return getHeader("Content-Type");
	}
	//---------------------------------------------------
	protected Locale locale = Locale.getDefault();
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	//--------------------------------------------------
	protected List<Cookie> cookies = new ArrayList<Cookie>();
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}
	public List<Cookie> getCookies() {
		return cookies;
	}
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}
	//----------------------------------------------------
}
