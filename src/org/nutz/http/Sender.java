package org.nutz.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.nutz.http.sender.FilePostSender;
import org.nutz.http.sender.GetSender;
import org.nutz.http.sender.PostSender;
import org.nutz.lang.Lang;

public abstract class Sender {

	public static Sender create(String url) {
		return create(Request.get(url));
	}

	public static Sender create(Request request) {
		if (request.isGet())
			return new GetSender(request);
		else if (request.isMultipart())
			return new FilePostSender(request);
		return new PostSender(request);
	}

	protected Request request;

	protected int timeout;

	protected URLConnection conn;

	protected Sender(Request request) {
		this.request = request;
	}

	public abstract Response send() throws HttpException;

	protected Response createResponse(Map<String, String> reHeaders) throws IOException {
		Response rep = null;
		if(reHeaders != null && reHeaders.get(null) != null){
			rep = new Response(reHeaders);
			if (rep.isOK()) {
				InputStream ins = new BufferedInputStream(conn.getInputStream());
				rep.setStream(ins);
			} else {
				rep.setStream(Lang.ins(""));
			}
		}
		return rep;
	}

	protected Map<String, String> getResponseHeader() {
		Map<String, String> reHeaders = new HashMap<String, String>();
		for (String key : conn.getHeaderFields().keySet()) {
			reHeaders.put(key, conn.getHeaderField(key));
		}
		return reHeaders;
	}

	protected void setupDoInputOutputFlag() {
		conn.setDoInput(true);
		conn.setDoOutput(true);
	}

	protected void openConnection() throws IOException {
		conn = request.getUrl().openConnection();
		if (timeout > 0)
			conn.setReadTimeout(timeout);
	}

	protected void setupRequestHeader() {
		Header header = request.getHeader();
		if (null != header) {
			for (String key : header.keys()) {
				conn.setRequestProperty(key, header.get(key));
			}
		}
		URL url = request.getUrl();
		conn.setRequestProperty("host", url.getHost() + ":" + url.getPort());
	}

}
