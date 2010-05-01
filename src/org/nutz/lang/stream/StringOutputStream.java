package org.nutz.lang.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 该类无法正确处理双字节字符,主要是write方法
 * <br/> 
 * Mark @Deprecated by Wendal (wendal1985@gmail.com)
 * @see java.io.StringBufferInputStream
 */
@Deprecated
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
