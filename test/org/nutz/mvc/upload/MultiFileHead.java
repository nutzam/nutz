package org.nutz.mvc.upload;

import java.io.IOException;

import org.nutz.lang.Lang;

public class MultiFileHead implements MultiReadable {

	private int cursor = 0;
	private byte[] cache = null;

	public MultiFileHead(String name, String fileName) {
		try {
			if (cache == null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Content-Disposition: form-data; name=\"").append(
						name).append("\"; filename=\"");
				sb.append(fileName).append("\"\r\n").append(
						"Content-Type: text/plain\r\n\r\n");
				cache = sb.toString().getBytes();
			}
		} catch (Exception e) {
			Lang.makeThrow("prepare plain content failed");
		}
	}

	public int read() throws Exception {
		if (cursor <= cache.length - 1)
			return cache[cursor++];
		return -1;
	}

	public long length() {
		return cache.length;
	}

	public void close() throws IOException {
		// do nothing
	}

}
