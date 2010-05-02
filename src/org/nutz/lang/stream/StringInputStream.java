package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.nutz.lang.Lang;

public class StringInputStream extends InputStream {

	private int cursor;
	private byte [] data;

	public StringInputStream(CharSequence s) {
		if (null != s)
			try {
				data = s.toString().getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				throw Lang.wrapThrow(e);
			}
		else
			data = new byte[0];
		cursor = 0;
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		if (cursor < data.length)
			return data[cursor++];
		return -1;
	}

	@Override
	public int available() throws IOException {
		return data.length - cursor;
	}

	@Override
	public long skip(long n) throws IOException {
		long len = 0;
		if (n > 0 && cursor < data.length){
			len = (cursor + n);
			if (len > data.length){
				len = data.length - cursor;
				cursor = data.length;
			}
		}else
			len = 0;
		return len;
	}

}
