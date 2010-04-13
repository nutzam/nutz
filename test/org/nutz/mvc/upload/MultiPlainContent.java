package org.nutz.mvc.upload;

import java.io.IOException;

import org.nutz.lang.Lang;

public class MultiPlainContent implements MultiReadable {

	private String name;
	private String value;

	// for read
	private int cursor = 0;
	private byte[] cache = null;

	public MultiPlainContent(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private String getPlainContent() {
		StringBuilder sb = new StringBuilder();
		sb.append("Content-Disposition: form-data; name=\"");
		sb.append(this.getName()).append("\"");
		sb.append("\r\n\r\n").append(this.getValue());
		return sb.toString();
	}

	private void prepareCache() {
		try {
			if (cache == null) {
				cache = this.getPlainContent().getBytes(
						MockProperties.getMockProperties().getProperty(
								"charset"));
			}
		} catch (Exception e) {
			Lang.makeThrow("prepare plain content failed");
		}
	}

	public int read() throws Exception {
		prepareCache();
		if (cursor <= cache.length - 1)
			return cache[cursor++];
		return -1;
	}

	public long length() {
		prepareCache();
		return cache.length;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void close() throws IOException {
		// do nothing
	}

	public void reset() {
		this.cursor = 0;
	}

}
