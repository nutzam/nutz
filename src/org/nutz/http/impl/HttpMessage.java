package org.nutz.http.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.nutz.http.server.NutListMap;

/**
 * 抽象的Http消息
 * @author wendal
 *
 */
public abstract class HttpMessage extends HttpObject {
	//----------------------------------------------------------------
	//Header相关
	protected NutListMap headers = new NutListMap();
	public NutListMap headers() {
		return headers;
	}
	//----------------------------------
	
	public int getContentLength() {
		return headers.getInt("Content-Length");
	}

	public void setContentLength(int len) {
		headers.setInt("Content-Length", len);
	}
	public void setContentType(String type) {
		headers.set("Content-Type", type);
	}
	public String getContentType() {
		return headers.get("Content-Type");
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
