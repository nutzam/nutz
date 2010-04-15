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
import javax.servlet.http.HttpSession;

import org.nutz.filepool.FilePool;
import org.nutz.http.Http;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StreamBuffer;
import org.nutz.lang.stream.StringOutputStream;

public class SimpleUploading implements Uploading {

	private static final char[] endName = {0x0D, 0x0A, 0x0D, 0x0A};

	public SimpleUploading(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	private int bufferSize;

	private UploadInfo info;

	public Map<String, Object> parse(HttpServletRequest req, String charset, FilePool tmps)
			throws UploadFailException {
		// Store upload info to sessioin
		info = new UploadInfo();
		HttpSession sess = req.getSession();
		if (null != sess) {
			sess.setAttribute(UploadInfo.SESSION_NAME, info);
		}
		info.sum = req.getContentLength();

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			// parse query strings
			String qs = req.getQueryString();
			if (null != qs) {
				String[] pairs = Strings.splitIgnoreBlank(qs, "&");
				for (String pair : pairs) {
					String[] pp = pair.split("=");
					if (pp.length > 1)
						params.put(pp[0], pp[1]);
					else
						params.put(pp[0], null);
				}
			}
			// Analyze the request data
			InputStream ins = req.getInputStream();
			// Buffer the stream
			if (!(ins instanceof BufferedInputStream)) {
				if (bufferSize > 0)
					ins = new BufferedInputStream(ins, bufferSize);
				else
					ins = new BufferedInputStream(ins);
			}
			// Check content type
			String contentType = req.getContentType();
			String s = "\r\n--" + Http.multipart.getBoundary(contentType);
			char[] endValue = s.toCharArray(); // expect boundary
			int[] right = new int[endValue.length]; // runtime cache
			int cursor;
			int c = 0;
			// skip first endField
			for (int i = 0; i < s.length(); i++) {
				c = ins.read();
				info.current++;
			}
			while (c != -1) {
				cursor = 0;
				right[cursor++] = ins.read();
				info.current++;
				if (right[0] == -1) {
					right[cursor++] = -1;
				} else {
					right[cursor++] = ins.read();
					info.current++;
				}
				if (right[1] == -1 || (right[0] == '-' && right[1] == '-'))
					break;
				// read field title
				StreamBuffer sb = new StreamBuffer();
				while (cursor < endName.length) {
					c = ins.read();
					if (c == -1)
						break;
					info.current++;

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
					File tmp = null;
					OutputStream ots;
					if (Strings.isBlank(meta.getFileLocalPath())) {
						ots = new StringOutputStream(new StringBuilder());
					} else {
						tmp = tmps.createFile(meta.getFileExtension());
						ots = new BufferedOutputStream(new FileOutputStream(tmp));
					}
					while (c != -1 && cursor < endValue.length) {
						c = ins.read();
						info.current++;
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
					if (null != tmp
						&& !Strings.isBlank(meta.getFileLocalPath())
						&& tmp.length() > 0)
						params.put(meta.getName(), new TempFile(meta, tmp));
				}
				// if the field is a post value store to the map
				else {
					sb = new StreamBuffer();
					while (c != -1 && cursor < endValue.length) {
						c = ins.read();
						info.current++;
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
			}
			return params;
		}
		catch (IOException e) {
			throw new UploadFailException(e);
		}
		finally {
			if (null != sess)
				sess.removeAttribute(UploadInfo.class.getName());
		}
	}

}
