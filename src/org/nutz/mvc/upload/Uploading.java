package org.nutz.mvc.upload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.filepool.FilePool;
import org.nutz.http.Http;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StreamBuffer;

class Uploading {

	private static final char[] endName = { 0x0D, 0x0A, 0x0D, 0x0A };

	Map<String, Object> params;

	public Uploading parse(HttpServletRequest request, String charset, FilePool tmpFiles) throws IOException {
		params = new HashMap<String, Object>();
		// Analyze the request data
		InputStream ins = request.getInputStream();
		String contentType = request.getContentType();
		String s = "\r\n--" + Http.multipart.getBoundary(contentType);
		char[] endValue = s.toCharArray(); // expect boundary
		int[] right = new int[endValue.length]; // runtime cache
		int cursor;
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
			FieldMeta meta = new FieldMeta(sb.toString(charset));
			cursor = 0;
			// if the field is file store to a tmp file
			if (meta.isFile()) {
				File tmp = tmpFiles.createFile(meta.getFileExtension());
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
				params.put(meta.getName(), new TempFile(meta, tmp));
			}
			// if the field is a post value store to the map
			else {
				sb = new StreamBuffer();
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
				params.put(meta.getName(), sb.toString(charset));
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
		return this;
	}
}
