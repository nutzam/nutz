package org.nutz.lang.encrypt;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.nutz.lang.Lang;

public class MsgDigestOutputStream extends FilterOutputStream {
	
	private MessageDigest md;

	public MsgDigestOutputStream(OutputStream out, MessageDigest md) {
		super(out);
		this.md = md;
	}
	
	public MsgDigestOutputStream(OutputStream out, String name) {
		super(out);
		try {
			this.md = MessageDigest.getInstance(name);
		}
		catch (NoSuchAlgorithmException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void write(byte[] b) throws IOException {
		this.out.write(b);
		md.update(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		this.out.write(b, off, len);
		md.update(b, off, len);
	}
	
	public void write(int b) throws IOException {
		this.out.write(b);
		md.update((byte)b);
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

}
