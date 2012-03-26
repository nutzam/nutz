package org.nutz.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.nutz.http.sender.FilePostSender;
import org.nutz.http.sender.GetSender;
import org.nutz.http.sender.PostSender;
import org.nutz.lang.Lang;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
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

	protected Response createResponse(Map<String, String> reHeaders)
			throws IOException {
		Response rep = null;
		if (reHeaders != null && reHeaders.get(null) != null) {
			rep = new Response(reHeaders);
			if (rep.isOK()) {
				InputStream is1 = conn.getInputStream();
				InputStream is2 = null;
				String encoding = conn.getContentEncoding();
				// 如果采用了压缩,则需要处理否则都是乱码
				if (encoding != null && encoding.contains("gzip")) {
					is2 = new GZIPInputStream(is1);
				} else if (encoding != null && encoding.contains("deflate")) {
					is2 = new InflaterInputStream(is1);
				} else {
					is2 = is1;
				}

				BufferedInputStream is = new BufferedInputStream(is2);
				rep.setStream(is);
			}

			else
				rep.setStream(Lang.ins(""));
		}
		return rep;
	}

	protected Map<String, String> getResponseHeader() {
		Map<String, String> reHeaders = new HashMap<String, String>();
		for (Entry<String, List<String>> en : conn.getHeaderFields().entrySet()) {
			List<String> val = en.getValue();
			if (null != val && val.size() > 0)
				reHeaders.put(en.getKey(), en.getValue().get(0));
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
		URL url = request.getUrl();
		String host = url.getHost();
		if (url.getPort() > 0 && url.getPort() != 80)
			host += ":" + url.getPort();
		conn.setRequestProperty("Host", host);
		Header header = request.getHeader();
		if (null != header)
			for (Entry<String, String> entry : header.getAll())
				conn.addRequestProperty(entry.getKey(), entry.getValue());
	}

}
