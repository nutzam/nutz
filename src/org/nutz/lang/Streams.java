package org.nutz.lang;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Streams {
	public static boolean equals(InputStream sA, InputStream sB) throws IOException {
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

	public static InputStream fileIn(String path) {
		return fileIn(Files.findFile(path));
	}

	public static InputStream fileIn(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static Reader fileInr(File file) {
		try {
			return new InputStreamReader(fileIn(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static Reader fileInr(String path) {
		return fileInr(Files.findFile(path));
	}

	public static OutputStream fileOut(String path) {
		return fileOut(Files.findFile(path));
	}

	public static OutputStream fileOut(File file) {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static Writer fileOutw(String path) {
		return fileOutw(Files.findFile(path));
	}

	public static Writer fileOutw(File file) {
		try {
			return new OutputStreamWriter(fileOut(file), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}
}
