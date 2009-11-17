package org.nutz.mvc.upload;

import java.io.IOException;
import java.io.InputStream;

public class UploadInfo {

	private int sum;

	private int current;

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int read(InputStream ins) throws IOException {
		current++;
		return ins.read();
	}
}
