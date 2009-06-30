package org.nutz.lang.stream;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {

	private StringBuilder sb;

	public StringOutputStream(StringBuilder sb) {
		this.sb = sb;
	}

	@Override
	public void write(int b) throws IOException {
		sb.append((char) b);
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

}
