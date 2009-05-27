package com.zzh.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import com.zzh.http.header.J2EE;
import com.zzh.lang.Lang;

public class Http {

	public static void main(String[] args) {
		Cookie cookie = new Cookie();
		J2EE.setSessionID(cookie, "6EAFAF28BB56CF95061C403CB447B154");
		// cookie.set("ABC", "DDD");
		Header header = header(cookie);
		// JSESSIONID=A77F5D4960669B7375EFFB081A8CC9C7
		String url = "http://localhost:8888/test/c/d?A=123";
		Response re = Sender.create(
				Request.create(url, Request.METHOD.GET, "{id:12,tt:66}", header)).send();
		System.out.println(J2EE.getSessionID(re));
		System.out.println(re.printAll());
	}

	public static Header header() {
		return header(null);
	}

	public static Header header(Cookie cookie) {
		Header header = new Header();
		header.set("user-agent", "Nutz.Robot");
		header.set("accept-encoding", "gzip,deflate,bzip2,sdch");
		header.set("accept", "text/xml,application/xml,application/xhtml+xml,text/html;"
				+ "q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		header.set("accept-language", "en-US,en,zh,zh-CN");
		header.set("accept-charset", "ISO-8859-1,*,utf-8");
		header.set("connection", "Keep-Alive");
		header.set("cache-control", "max-age=0");
		if (null != cookie)
			header.set("cookie", cookie.remove("Path").toString());
		return header;
	}

	public static class multipart {
		public static String getBoundary(String contentType) {
			if (null == contentType)
				return null;
			int pos = contentType.indexOf(";");
			if (pos <= 10)
				return null;
			if (!contentType.substring(0, pos).equalsIgnoreCase("multipart/form-data"))
				return null;
			pos = contentType.indexOf("=", pos);
			if (pos < 0)
				return null;
			return contentType.substring(pos + 1);
		}

		public static String formatName(String name, String filename, String contentType) {
			StringBuilder sb = new StringBuilder();
			sb.append("Content-Disposition: form-data; name=\"");
			sb.append(name);
			sb.append("\"");
			if (null != filename)
				sb.append("; filename=\"" + filename + "\"");
			if (null != contentType)
				sb.append("\nContent-Type: " + contentType);
			sb.append('\n' + '\n');
			return sb.toString();
		}

		public static String formatName(String name) {
			return formatName(name, null, null);
		}
	}

	public static Response get(String url) {
		return Sender.create(Request.get(url)).send();
	}

	public static String post(String url, Map<String, Object> params, String inenc, String reenc) {
		StringBuilder sb = new StringBuilder();
		try {
			URL oUrl = new URL(url);
			URLConnection conn = oUrl.openConnection();
			if (null != params && params.size() > 0) {
				conn.setDoOutput(true);
				Writer w = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
				Iterator<String> it = params.keySet().iterator();
				String key = it.next();
				Object v = params.get(key);
				w.write(URLEncoder.encode(key, inenc));
				w.write('=');
				if (null != v)
					w.write(URLEncoder.encode(params.get(key).toString(), inenc));
				while (it.hasNext()) {
					key = it.next();
					w.write('&');
					w.write(URLEncoder.encode(key, inenc));
					w.write('=');
					v = params.get(key);
					if (null != v)
						w.write(URLEncoder.encode(params.get(key).toString(), inenc));
				}
				w.flush();
				w.close();
				w = null;
			}
			// Get the response
			BufferedReader br = new BufferedReader(null == reenc ? new InputStreamReader(conn
					.getInputStream()) : new InputStreamReader(conn.getInputStream(), reenc));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append('\n');
			}
			br.close();
			br = null;
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
		return sb.toString();
	}

	public static String encode(Object s) throws UnsupportedEncodingException {
		if (null == s)
			return "";
		return URLEncoder.encode(s.toString(), "UTF-8");
	}

}
