package org.nutz.lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringOutputStream;


public class Localize {
	public static TimeZone getTimeZone(int off) {
		if (off == 0)
			return TimeZone.getTimeZone("GMT");
		else if (off > 0)
			return TimeZone.getTimeZone("GMT+" + off);
		return TimeZone.getTimeZone("GMT" + off);
	}

	public static TimeZone getTimeZone(Locale locale) {
		Calendar c = Calendar.getInstance(locale);
		return c.getTimeZone();
	}

	public static String convertAscii2Native(CharSequence s) {
		return convertAscii2Native(s, '\\');
	}

	public static String convertAscii2Native(CharSequence s, char mark) {
		if (null == s)
			return null;
		boolean prevIsEscapeChar = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == mark) {
				if (prevIsEscapeChar) {
					sb.append(c);
					prevIsEscapeChar = false;
				} else
					prevIsEscapeChar = true;
			} else if (c == 'u' && prevIsEscapeChar) {
				prevIsEscapeChar = false;
				StringBuffer code = new StringBuffer();
				for (int j = 0; j < 4; j++) {
					char b = s.charAt(++i);
					code.append(b);
				}
				c = (char) Integer.parseInt(code.toString(), 16);
				sb.append(c);
			} else
				sb.append(c);
		}
		return sb.toString();
	}

	public static void native2ascii(File src, File dest, String encoding) throws IOException {

		BufferedReader r = new BufferedReader((null == encoding ? new InputStreamReader(
				new FileInputStream(src)) : new InputStreamReader(new FileInputStream(src),
				encoding)));
		if (dest.exists())
			Files.deleteFile(dest);
		BufferedWriter w = new BufferedWriter(new FileWriter(dest));
		int c;
		while (-1 != (c = r.read())) {
			writeChar(w, c);
		}
		r.close();
		w.close();
	}

	public static String convertNative2Ascii(CharSequence s) throws IOException {
		if (null == s)
			return null;
		StringBuilder sb = new StringBuilder();
		OutputStream ops = new StringOutputStream(sb);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			writeChar(ops, c);
		}
		return sb.toString();
	}

	private static void writeChar(Writer w, int c) throws IOException {
		if (c > 0 && c < 256) {
			w.write((char) c);
		} else if (c > 256) {
			w.write("\\u");
			w.write(Strings.fillHex(c, 4));
		} else {
			w.write("-\\u");
			w.write(Strings.fillHex(c, 4));
		}
	}

	private static void writeChar(OutputStream ops, int c) throws IOException {
		if (c > 0 && c < 256) {
			ops.write(c);
		} else if (c > 256) {
			Streams.write(ops, new StringBuffer("\\u").append(Strings.fillHex(c, 4)));
		} else {
			Streams.write(ops, new StringBuffer("-\\u").append(Strings.fillHex(c, 4)));
		}
	}

	public static void convertEncoding(File src, String srcEncoding, File dest, String destEncoding)
			throws IOException {
		BufferedReader r = new BufferedReader((null == srcEncoding ? new InputStreamReader(
				new FileInputStream(src)) : new InputStreamReader(new FileInputStream(src),
				srcEncoding)));
		if (dest.exists())
			Files.deleteFile(dest);
		BufferedWriter w = new BufferedWriter(null == destEncoding ? new OutputStreamWriter(
				new FileOutputStream(dest)) : new OutputStreamWriter(new FileOutputStream(dest),
				destEncoding));
		int c;
		while (-1 != (c = r.read()))
			w.write(c);
		r.close();
		w.close();
	}
}
