package com.zzh.http.header;

import com.zzh.http.Cookie;
import com.zzh.http.Request;
import com.zzh.http.Response;

public class J2EE {

	public static String getSessionID(Response response) {
		return getSessionID(response.getCookie());
	}

	public static String getSessionID(Request request) {
		return getSessionID(request.getCookie());
	}

	public static String getSessionID(Cookie cookie) {
		if (null == cookie)
			return null;
		return cookie.get("JSESSIONID");
	}

	public static void setSessionID(Cookie cookie, String sessID) {
		if (null != cookie)
			cookie.set("JSESSIONID", sessID);
	}

	public static void setSessionID(Response resp, String sessID) {
		setSessionID(resp.getCookie(), sessID);
	}

	public static void setSessionID(Request req, String sessID) {
		setSessionID(req.getCookie(), sessID);
	}

}
