package org.nutz.http.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nutz.lang.Lang;

public class AbstractHttpObject2 extends AbstractHttpObject {
	//----------------------------------------------------------------
	//Header相关
	protected Map<String, String> headers = new HashMap<String, String>();
	
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public long getDateHeader(String name) {
		String str = headers.get(name);
		if (str == null)
			return 0;
		try {
			return Https.httpData(str).getTime();
		} catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public int getIntHeader(String name) {
		String str = headers.get(name);
		if (str == null)
			return 0;
		return Integer.parseInt(str);
	}
	
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}
	
	public Enumeration<String> getHeaders(String name) {
		ArrayList<String> list = new ArrayList<String>(1);
		String header = getHeader(name);
		if (header != null)
			list.add(header);
		return Collections.enumeration(list);
	}
	
	public String getContentType() {
		return getHeader("Content-Type");
	}
	
	public int getContentLength() {
		return getIntHeader("Content-Length");
	}
	//---------------------------------------------------
	protected Locale locale = Locale.getDefault();
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
