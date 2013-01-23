package org.nutz.lang.encrypt;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.nutz.lang.Lang;

public class MsgDigestInputStream extends FilterInputStream {

	private MessageDigest md;

	public MsgDigestInputStream(InputStream in, MessageDigest md) {
		super(in);
		this.md = md;
	}
	
	public MsgDigestInputStream(InputStream in, String name) {
		super(in);
		try {
			this.md = MessageDigest.getInstance(name);
		}
		catch (NoSuchAlgorithmException e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public int read() throws IOException {
		int b = this.in.read();
		md.update((byte)b);
		return b;
	}
	
	public int read(byte[] b) throws IOException {
		int len = this.in.read(b);
		md.update(b, 0 , len);
		return len;
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		int len2 = this.in.read(b, off, len);
		md.update(b, off, len2);
		return len2;
	}
	
	/**
	 * 获取摘要, 同时MessageDigest也被重置了
	 */
	public String digest() {
		return Lang.fixedHexString(md.digest());
	}
	
	public boolean markSupported() {
		return false;
	}
	
	public synchronized void reset() throws IOException {
		super.reset();
		md.reset();
	}
}
