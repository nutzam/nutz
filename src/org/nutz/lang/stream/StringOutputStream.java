package org.nutz.lang.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {

	private StringBuilder sb;
	private ByteArrayOutputStream baos;
	
	public StringOutputStream(StringBuilder sb) {
		this.sb = sb;
		baos = new ByteArrayOutputStream();
	}

	/**
	 * 完成本方法后,确认字符串已经完成写入后,务必调用flash方法!
	 */
	@Override
	public void write(int b) throws IOException {
		baos.write(b);
	}
	
	/**
	 * 使用StringBuilder前,务必调用
	 */
	@Override
	public void flush() throws IOException {
		super.flush();
		baos.flush();
		if (baos.size() > 0){
			sb.append(new String(baos.toByteArray()));
			baos.reset();
		}
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

}
