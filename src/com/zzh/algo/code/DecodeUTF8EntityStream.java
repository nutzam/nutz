package com.zzh.algo.code;

import java.io.IOException;
import java.io.OutputStream;

public class DecodeUTF8EntityStream extends OutputStream {
	private OutputStream ops;
	private int[] buf;
	private int index;

	public DecodeUTF8EntityStream(OutputStream ops) {
		this.ops = ops;
		this.buf = new int[16];
		index = 0;
	}

	@Override
	public void write(int b) throws IOException {
		if (index == 0 && b == '&')
			buf[index++] = b;
		else if (index == 1) {
			if (b == '#') {
				buf[index++] = b;
			} else {
				index = 0;
				ops.write('&');
				ops.write(b);
			}
		} else if (b == ';') {
			if (index > 3) {
				char[] cs = new char[index - 2];
				for (int i = 0; i < cs.length; i++)
					cs[i] = (char) buf[i + 2];
				char c = (char) Integer.valueOf(String.valueOf(cs)).intValue();
				index = 0;
				ops.write(c);
			} else {
				for (int i = 0; i < index; i++)
					ops.write(buf[i]);
				ops.write(b);
				index = 0;
			}

		} else if (index > 0) {
			if (b < 48 || b > 57) {// is not number
				for (int i = 0; i < index; i++)
					ops.write(buf[i]);
				ops.write(b);
				index = 0;
			} else
				buf[index++] = b;
		} else {
			ops.write(b);
		}
	}

	@Override
	public void close() throws IOException {
		for (int i = 0; i < index; i++)
			ops.write(buf[i]);
		index = 0;
		super.close();
	}

}
