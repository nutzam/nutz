package org.nutz.mvc.upload;

import java.io.IOException;

import org.nutz.lang.Lang;

public class MultiSeparator implements MultiReadable {

	private int cursor = 0;
	private byte[] cache = null;

	public MultiSeparator() {
		try {
			if (cache == null) {
				cache = ("\r\n--"
						+ MockProperties.getMockProperties().getProperty(
								"boundary") + "\r\n").getBytes();
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
