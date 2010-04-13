package org.nutz.mock.servlet.multipart.inputing;

import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.Lang;

public class StringInputing implements Inputing {

	private InputStream ins;

	public StringInputing(String str) {
		ins = Lang.ins(str);
	}

	public int read() {
		try {
			return ins.read();
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public long size() {
		try {
			return ins.available();
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void close() throws IOException {}

	public void init() throws IOException {}

}
