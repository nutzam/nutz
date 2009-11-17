package org.nutz.mvc.upload;

import java.io.BufferedInputStream;
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

	private static final char[] endName = {0x0D, 0x0A, 0x0D, 0x0A};

	private Map<String, Object> params;

	private UploadInfo info;

	public void parse(HttpServletRequest request, String charset, FilePool tmpFiles)
			throws UploadFailException {
		// Store upload info to sessioin
		info = new UploadInfo();
		request.getSession().setAttribute(UploadInfo.class.getName(), info);
		info.setSum(request.getContentLength());

		try {
			params = new HashMap<String, Object>();
			// Analyze the request data
			InputStream ins = request.getInputStream();
			// Buffer the stream
			if (!(ins instanceof BufferedInputStream))
				ins = new BufferedInputStream(ins);
			// Check content type
			String contentType = request.getContentType();
			String s = "\r\n--" + Http.multipart.getBoundary(contentType);
			char[] endValue = s.toCharArray(); // expect boundary
			int[] right = new int[endValue.length]; // runtime cache
			int cursor;
			int c = 0;
			// skip first endField
			for (int i = 0; i < s.length(); i++)
				c = info.read(ins);
			while (c != -1) {
				cursor = 0;
				right[cursor++] = info.read(ins);
				right[cursor++] = right[0] == -1 ? -1 : info.read(ins);
				if (right[1] == -1 || (right[0] == '-' && right[1] == '-'))
					break;
				// read field title
				StreamBuffer sb = new StreamBuffer();
				while (cursor < endName.length && (c = info.read(ins)) != -1) {
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
						c = info.read(ins);
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
						c = info.read(ins);
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
		} catch (IOException e) {
			throw new UploadFailException(e);
		} finally {
			request.getSession().removeAttribute(UploadInfo.class.getName());
		}
	}

	public Map<String, Object> getParams() {
		return params;
	}

}
