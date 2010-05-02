package org.nutz.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.io.Writer;

/**
 * 提供了一组创建 Reader/Writer/InputStream/OutputStream 的便利函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public abstract class Streams {

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
		ops.write(cs.toString().getBytes());
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
			}
			catch (IOException e) {
				return false;
			}
		return true;
	}

	/**
	 * 为一个输入流包裹一个缓冲流。如果这个输入流本身就是缓冲流，则直接返回
	 * 
	 * @param ins
	 *            输入流。
	 * @return 缓冲流
	 */
	public static BufferedInputStream buff(InputStream ins) {
		if (ins instanceof BufferedInputStream)
			return (BufferedInputStream) ins;
		return new BufferedInputStream(ins);
	}

	/**
	 * 为一个输出流包裹一个缓冲流。如果这个输出流本身就是缓冲流，则直接返回
	 * 
	 * @param ops
	 *            输出流。
	 * @return 缓冲流
	 */
	public static BufferedOutputStream buff(OutputStream ops) {
		if (ops instanceof BufferedOutputStream)
			return (BufferedOutputStream) ops;
		return new BufferedOutputStream(ops);
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
					ins = new FileInputStream(f);
				}
				catch (FileNotFoundException e) {}
		}
		return buff(ins);
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
			return buff(new FileInputStream(file));
		}
		catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8文本输入流
	 * 
	 * @param path
	 *            文件路径
	 * @return 文本输入流
	 */
	public static Reader fileInr(String path) {
		return new InputStreamReader(fileIn(path), Encoding.CHARSET_UTF8);
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8 文本输入流
	 * 
	 * @param file
	 *            文件
	 * @return 文本输入流
	 */
	public static Reader fileInr(File file) {
		return new InputStreamReader(fileIn(file), Encoding.CHARSET_UTF8);
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
			return buff(new FileOutputStream(file));
		}
		catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据一个文件路径建立一个 UTF-8 文本输出流
	 * 
	 * @param path
	 *            文件路径
	 * @return 文本输出流
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
		return new OutputStreamWriter(fileOut(file), Encoding.CHARSET_UTF8);
	}
}
