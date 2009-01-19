package com.zzh.lang.stream;

import java.io.IOException;
import java.io.OutputStream;

public class CharOutputStream extends OutputStream {

	private StringBuilder sb;

	public CharOutputStream(StringBuilder sb) {
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
