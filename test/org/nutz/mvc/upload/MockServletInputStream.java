package org.nutz.mvc.upload;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import org.nutz.lang.Lang;

public class MockServletInputStream extends ServletInputStream {

	private MultipartBody mb;

	public MockServletInputStream(MultipartBody mb) {
		this.mb = mb;
	}

	@Override
	public int read() throws IOException {
		try {
			return mb.read();
		} catch (Exception e) {
			Lang.makeThrow("read fail in MockServletInputStream.");
		}
		return -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int length = 0;
		for (int i = 1; i <= len; i++) {
			int re = this.read();
			if (re == -1)
				break;
			else {
				b[off++] = (byte) re;
				length++;
			}
		}
		if (length == len)
			return len;
		if (length > 0)
			return length;
		return -1;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	@Override
	public int available() throws IOException {
		// TODO Auto-generated method stub
		return super.available();
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		// do nothing
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new IOException();
	}

	@Override
	public long skip(long n) throws IOException {
		throw new IOException();
	}

	public MultipartBody getMb() {
		return mb;
	}

	public void setMb(MultipartBody mb) {
		this.mb = mb;
	}

}
