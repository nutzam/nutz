package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;

public class StringInputStream extends InputStream {

	private char[] chars;
	private int cursor;
	private int mark;

	private StringInputStream() {
		super();
		cursor = 0;
		mark = 0;
	}

	public StringInputStream(CharSequence s) {
		this();
		if (null != s)
			chars = s.toString().toCharArray();
		else
			chars = new char[0];
	}

	@Override
	public synchronized void mark(int readlimit) {
		this.mark = readlimit;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public int read() throws IOException {
		if (cursor >= chars.length)
			return -1;
		return chars[cursor++];
	}

	@Override
	public int available() throws IOException {
		return chars.length;
	}

	@Override
	public synchronized void reset() throws IOException {
		cursor = mark;
	}

	@Override
	public long skip(long n) throws IOException {
		int len = chars.length;
		if (len > cursor + n) {
			cursor += n;
			return n;
		}
		int d = len - 1 - cursor;
		cursor = len;
		return d;
	}

}
