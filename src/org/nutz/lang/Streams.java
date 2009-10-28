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
import java.nio.charset.Charset;

/**
 * 提供了一组创建 Reader/Writer/InputStream/OutputStream 的便利函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Streams {

	public static final String DEFAULT_ENCODING = Charset.forName("UTF-8").displayName();

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
		InputStream ins = Files.findFileAsStream(path);
		if (null == ins) {
			File f = Files.findFile(path);
			if (null != f)
				try {
					return new FileInputStream(f);
				} catch (FileNotFoundException e) {}
		}
		return ins;
	}

	public static InputStream fileIn(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static Reader fileInr(File file) {
		return fileInr(file, DEFAULT_ENCODING);
	}

	public static Reader fileInr(String path) {
		return fileInr(path, DEFAULT_ENCODING);
	}

	public static Reader fileInr(File file, String encoding) {
		try {
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			return new InputStreamReader(fileIn(file), encoding);
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static Reader fileInr(String path, String encoding) {
		try {
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			return new InputStreamReader(fileIn(path), encoding);
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
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
		return fileOutw(file, DEFAULT_ENCODING);
	}

	public static Writer fileOutw(String path, String encoding) {
		return fileOutw(Files.findFile(path), encoding);
	}

	public static Writer fileOutw(File file, String encoding) {
		try {
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			return new OutputStreamWriter(fileOut(file), encoding);
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}
}
