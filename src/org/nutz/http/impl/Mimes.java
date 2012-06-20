package org.nutz.http.impl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public final class Mimes {

	protected static final Log log = Logs.get();
	
	protected static Map<String, String> mimes = new HashMap<String, String>();
	public static String guess(String suffix) {
		String mime = mimes.get(suffix);
		if (mime == null)
			mime = "application/octet-stream";
		if (log.isDebugEnabled())
			log.debugf("suffix=%s mime=%s",suffix, mime);
		return mime;
	}
	
	static {
		mimes.put("txt", "text/plain");
		mimes.put("html", "text/html");
		mimes.put("js", "application/x-javascript");
		mimes.put("css", "text/css");
		mimes.put("ico", "image/x-icon");
		mimes.put("xml", "text/xml");
		mimes.put("jpg", "image/jpeg");
		mimes.put("png", "image/png");
		mimes.put("gif", "image/gif");
	}
}
