package com.zzh.lang;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams {
	public static boolean isEquals(InputStream sA, InputStream sB) throws IOException {
		int dA;
		while ((dA = sA.read()) != -1) {
			if (dA != sB.read())
				return false;
		}
		if (sB.read() != -1)
			return false;
		return true;
	}

	public static void write(OutputStream ops, CharSequence cs) throws IOException {
		if (null == cs || null == ops)
			return;
		for (int i = 0; i < cs.length(); i++)
			ops.write(cs.charAt(i));
	}

	public static boolean safeClose(Closeable cb) {
		if (null != cb)
			try {
				cb.close();
			} catch (IOException e) {
				return false;
			}
		return true;
	}
}
