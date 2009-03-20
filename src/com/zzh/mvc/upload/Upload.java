package com.zzh.mvc.upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.filepool.FilePool;
import com.zzh.http.Http;
import com.zzh.lang.Strings;
import com.zzh.lang.stream.StreamBuffer;
import com.zzh.mvc.Controllor;

public class Upload implements Controllor {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParams(ServletRequest request) {
		return (Map<String, Object>) request.getAttribute(Upload.class.getName());
	}

	public Upload(FilePool tmpFiles) {
		this.tmpFiles = tmpFiles;
		charset = "UTF-8";
	}

	private FilePool tmpFiles;
	private String charset;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RequestParser parser = new RequestParser(request);
		request.setAttribute(Upload.class.getName(), parser.params);
		return parser.params;
	}

	private static final char[] endName = { 0x0D, 0x0A, 0x0D, 0x0A };

	protected class RequestParser {

		private InputStream ins;
		private String contentType;
		private char[] endValue; // expect boundary
		private int[] right; // runtime cache
		private int cursor;
		private Map<String, Object> params;

		public RequestParser(HttpServletRequest request) throws IOException {
			params = new HashMap<String, Object>();
			// Analyze the request data
			this.ins = request.getInputStream();
			contentType = request.getContentType();
			String s = "\r\n--" + Http.multipart.getBoundary(contentType);
			endValue = s.toCharArray();
			right = new int[endValue.length];
			int c = 0;
			// skip first endField
			for (int i = 0; i < s.length(); i++)
				c = ins.read();
			while (c != -1) {
				cursor = 0;
				right[cursor++] = ins.read();
				right[cursor++] = right[0] == -1 ? -1 : ins.read();
				if (right[1] == -1 || (right[0] == '-' && right[1] == '-'))
					break;
				// read field title
				StreamBuffer sb = new StreamBuffer();
				while (cursor < endName.length && (c = ins.read()) != -1) {
					if (c == endName[cursor]) {
						right[cursor++] = c;
					} else {
						if (cursor > 0) {
							for (int i = 0; i < cursor; i++)
								sb.write(right[i]);
						}
						sb.write(c);
						cursor = 0;
					}
				}
				// parse name
				FieldTitle title = new FieldTitle(sb.toString(charset));
				cursor = 0;
				// if the field is file store to a tmp file
				if (title.isFile()) {
					File tmp = tmpFiles.createFile(title.getFileExtension());
					OutputStream ots = new BufferedOutputStream(new FileOutputStream(tmp));
					while (c != -1 && cursor < endValue.length) {
						c = ins.read();
						if (c == endValue[cursor]) {
							right[cursor++] = c;
						} else {
							if (cursor > 0) {
								for (int i = 0; i < cursor; i++)
									ots.write(right[i]);
							}
							ots.write(c);
							cursor = 0;
						}
					}
					ots.close();
					params.put(title.getName(), new UploadedFile(title, tmp));
				} else {// if the field is a post value stop to the map
					sb = new StreamBuffer();
					// InputStreamReader reader = new InputStreamReader(ins,
					// charset);
					while (c != -1 && cursor < endValue.length) {
						c = ins.read();
						if (c == endValue[cursor]) {
							right[cursor++] = c;
						} else {
							if (cursor > 0) {
								for (int i = 0; i < cursor; i++)
									sb.write(right[i]);
							}
							sb.write(c);
							cursor = 0;
						}
					}
					params.put(title.getName(), sb.toString(charset));
				}
				// parse query strings
				s = request.getQueryString();
				if (null != s) {
					String[] pairs = Strings.splitIgnoreBlank(s, "&");
					for (String pair : pairs) {
						String[] pp = pair.split("=");
						if (pp.length > 1)
							params.put(pp[0], pp[1]);
						else
							params.put(pp[0], null);
					}
				}
			}
		}
	}

	public static class FieldTitle {

		FieldTitle(String s) {
			map = new HashMap<String, String>();
			String[] ss = Strings.splitIgnoreBlank(s, "[\n;]");
			for (String pair : ss) {
				String name = pair.split("[:=]")[0];
				String value = pair.replaceAll("^[^=:]*[=:]", "");
				map.put(Strings.trim(name), formatValue(value));
			}
		}

		private static String formatValue(String s) {
			s = Strings.trim(s);
			if (s.charAt(0) == '"')
				return s.substring(1, s.length() - 1);
			return s;
		}

		Map<String, String> map;

		public String getContentType() {
			return map.get("Content-Type");
		}

		public String getName() {
			return map.get("name");
		}

		public String getFileLocalPath() {
			return map.get("filename");
		}

		public String getFileLocalName() {
			return (new File(getFileLocalPath())).getName();
		}

		public String getFileExtension() {
			String name = getFileLocalPath();
			int pos = name.lastIndexOf('.');
			return name.substring(pos);
		}

		public String getContentDisposition() {
			return map.get("Content-Disposition");
		}

		public boolean isFile() {
			return null != getContentType() && null != getFileLocalPath();
		}
	}

}
