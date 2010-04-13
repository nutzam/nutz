package org.nutz.mock.servlet.multipart;

import java.io.IOException;

import org.nutz.mock.servlet.multipart.inputing.Inputing;

public abstract class MultipartItem {

	public MultipartItem(String boundary) {
		inputs = new Inputing[5];
		last = 0;
		index = 0;
	}

	private Inputing[] inputs;
	private int last;
	private int index;
	private Inputing current;

	protected void addInputing(Inputing in) {
		inputs[last++] = in;
	}

	public long size() {
		long re = 0;
		for (int i = 0; i < last; i++) {
			re += inputs[i].size();
		}
		return re;
	}

	public int read() throws IOException {
		int d = current.read();
		while (d == -1) {
			if (index >= last)
				return d;
			current = inputs[++index];
			d = current.read();
		}
		return d;
	}

	public void init() throws IOException {
		for (int i = 0; i < last; i++) {
			inputs[i].init();
		}
	}

	public void close() throws IOException {
		for (int i = 0; i < last; i++) {
			inputs[i].close();
		}
	}

}
