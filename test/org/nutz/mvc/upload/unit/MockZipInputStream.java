package org.nutz.mvc.upload.unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class MockZipInputStream extends ServletInputStream {

	private InputStream ins;

	public MockZipInputStream(File file) throws FileNotFoundException {
		this.ins = new FileInputStream(file);
	};

	@Override
	public int read() throws IOException {
		return this.ins.read();
	}

	@Override
	public int available() throws IOException {
		return this.ins.available();
	}

	@Override
	public void close() throws IOException {
		this.ins.close();
	}

}
