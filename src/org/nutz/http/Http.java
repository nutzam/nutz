package org.nutz.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.nutz.http.Request.METHOD;

public class Http {

	public static void main(String[] args) {
		// JSESSIONID=A77F5D4960669B7375EFFB081A8CC9C7
		String url = "http://localhost:8888/test/c/d?A=123";
		Request request = Request.create(url, Request.METHOD.GET, "{id:12,tt:66}");
		request.setCookie(new Cookie("JSESSIONID=5F081DC2E0F1780EBA88566E8D605EA4"));
		Response re = Sender.create(request).send();
		System.out.println(re.getHeader().toString());
		System.out.println(re.getContent());
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
		return Sender.create(Request.create(url, METHOD.POST, params, null)).send().getContent();
	}

	public static String encode(Object s) throws UnsupportedEncodingException {
		if (null == s)
			return "";
		return URLEncoder.encode(s.toString(), "UTF-8");
	}

}
