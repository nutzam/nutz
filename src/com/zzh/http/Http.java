package com.zzh.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import com.zzh.lang.Lang;

public class Http {

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

	public static String get(String url, String encoding) {
		StringBuilder sb = new StringBuilder();
		try {
			URL oUrl = new URL(url);
			URLConnection conn = oUrl.openConnection();
			BufferedReader br = new BufferedReader(null == encoding ? new InputStreamReader(conn
					.getInputStream()) : new InputStreamReader(conn.getInputStream(), encoding));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append('\n');
			}
			br.close();
			br = null;
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return sb.toString();
	}

	public static String post(String url, Map<String, Object> data, String inenc, String reenc) {
		StringBuilder sb = new StringBuilder();
		try {
			URL oUrl = new URL(url);
			URLConnection conn = oUrl.openConnection();
			if (null != data && data.size() > 0) {
				conn.setDoOutput(true);
				Writer w = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
				Iterator<String> it = data.keySet().iterator();
				String key = it.next();
				Object v = data.get(key);
				w.write(URLEncoder.encode(key, inenc));
				w.write('=');
				if (null != v)
					w.write(URLEncoder.encode(data.get(key).toString(), inenc));
				while (it.hasNext()) {
					key = it.next();
					w.write('&');
					w.write(URLEncoder.encode(key, inenc));
					w.write('=');
					v = data.get(key);
					if (null != v)
						w.write(URLEncoder.encode(data.get(key).toString(), inenc));
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
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return sb.toString();
	}

}
