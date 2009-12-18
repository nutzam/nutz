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
 * @author Wendal(wendal1985@gmail.com)
 */
public abstract class Streams {

	public static final String DEFAULT_ENCODING = Charset.forName("UTF-8").displayName();

	/**
	 * 判断两个输入流是否严格相等
	 */
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

	/**
	 * 将一段文本全部写入一个输出流
	 * 
	 * @param ops
	 *            输出流
	 * @param cs
	 *            文本
	 * @throws IOException
	 */
	public static void write(OutputStream ops, CharSequence cs) throws IOException {
		if (null == cs || null == ops)
			return;
		for (int i = 0; i < cs.length(); i++)
			ops.write(cs.charAt(i));
	}

	/**
	 * 关闭一个可关闭对象，可以接受 null。如果成功关闭，返回 true，发生异常 返回 false
	 * 
	 * @param cb
	 *            可关闭对象
	 * @return 是否成功关闭
	 */
	public static boolean safeClose(Closeable cb) {
		if (null != cb)
			try {
				cb.close();
			} catch (IOException e) {
				return false;
			}
		return true;
	}

	/**
	 * 根据一个文件路径建立一个输入流
	 * 
	 * @param path
	 *            文件路径
	 * @return 输入流
	 */
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

	/**
	 * 根据一个文件路径建立一个输入流
	 * 
	 * @param file
	 *            文件
	 * @return 输入流
	 */
	public static InputStream fileIn(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8 文本输入流
	 * 
	 * @param file
	 *            文件
	 * @return 文本输入流
	 */
	public static Reader fileInr(File file) {
		return fileInr(file, DEFAULT_ENCODING);
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8文本输入流
	 * 
	 * @param path
	 *            文件路径
	 * @return 文本输入流
	 */
	public static Reader fileInr(String path) {
		return fileInr(path, DEFAULT_ENCODING);
	}

	/**
	 * 根据一个文件，以及指定的编码方式，建立一个 文本输入流
	 * 
	 * @param file
	 *            文件
	 * @param encoding
	 *            文本文件编码方式
	 * @return 文本输入流
	 * 
	 * @see java.nio.charset.Charset
	 */
	public static Reader fileInr(File file, String encoding) {
		try {
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			return new InputStreamReader(fileIn(file), encoding);
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据一个文件路径，以及指定的编码方式，建立一个 文本输入流
	 * 
	 * @param path
	 *            文件路径
	 * @param encoding
	 *            文本文件编码方式
	 * @return 文本输入流
	 * 
	 * @see java.nio.charset.Charset
	 */
	public static Reader fileInr(String path, String encoding) {
		try {
			if (encoding == null)
				encoding = DEFAULT_ENCODING;
			return new InputStreamReader(fileIn(path), encoding);
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据一个文件路径建立一个输出流
	 * 
	 * @param path
	 *            文件路径
	 * @return 输出流
	 */
	public static OutputStream fileOut(String path) {
		return fileOut(Files.findFile(path));
	}

	/**
	 * 根据一个文件建立一个输出流
	 * 
	 * @param file
	 *            文件
	 * @return 输出流
	 */
	public static OutputStream fileOut(File file) {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8 文本输出流
	 * 
	 * @param path
	 *            文件路径
	 * @return 输出流
	 */
	public static Writer fileOutw(String path) {
		return fileOutw(Files.findFile(path));
	}

	/**
	 * 根据一个文件建立一个 UTF-8 文本输出流
	 * 
	 * @param file
	 *            文件
	 * @return 输出流
	 */
	public static Writer fileOutw(File file) {
		return fileOutw(file, DEFAULT_ENCODING);
	}

	/**
	 * 根据一个文件路径，以及指定的编码方式，建立一个 文本输出流
	 * 
	 * @param path
	 *            文件路径
	 * @param encoding
	 *            文本文件编码方式
	 * @return 输出流
	 * @see java.nio.charset.Charset
	 */
	public static Writer fileOutw(String path, String encoding) {
		return fileOutw(Files.findFile(path), encoding);
	}

	/**
	 * 根据一个文件，以及指定的编码方式，建立一个 文本输出流
	 * 
	 * @param file
	 *            文件
	 * @param encoding
	 *            文本文件编码方式
	 * @return 输出流
	 * @see java.nio.charset.Charset
	 */
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
