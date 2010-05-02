package org.nutz.lang.stream;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {

	private StringBuilder sb;
	private byte [] data = new byte[12];
	private short index = 0;

	public StringOutputStream(StringBuilder sb) {
		this.sb = sb;
	}

	/**
	 * 完成本方法后,确认字符串已经完成写入后,务必调用flash方法!
	 */
	@Override
	public void write(int b) throws IOException {
		if (index < data.length)
			data[index++] = (byte)b;//传入的其实是byte
		else {
			sb.append(new String(data));
			index = 0;
			data[index++] = (byte)b;
		}
	}
	
	/**
	 * 使用StringBuilder前,务必调用
	 */
	@Override
	public void flush() throws IOException {
		super.flush();
		if (index > 0){
			sb.append(new String(data,0,index));
			index = 0;
		}
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

}
