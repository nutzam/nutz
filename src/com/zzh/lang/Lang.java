package com.zzh.lang;

import java.io.InputStream;

import com.zzh.lang.stream.CharInputStream;
import com.zzh.lang.stream.CharOutputStream;

public class Lang {

	public static <T> T nonNull(T t, String message) {
		if (t == null) {
			throw new NullPointerException(message);
		}
		return t;
	}

	public static RuntimeException wrapThrow(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		return new RuntimeException(e);
	}

	public static boolean equals(Object a1, Object a2) {
		if (a1 == a2)
			return true;
		if (a1 == null || a2 == null)
			return false;
		return a1.equals(a2);
	}

	public static InputStream ins(CharSequence cs) {
		return new CharInputStream(cs);
	}

	public static CharOutputStream ops() {
		return new CharOutputStream(new StringBuilder());
	}

	public static <T> T[] array(T... ele) {
		return ele;
	}

	public static <T> StringBuilder joinBy(String ptn, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T t : o)
			sb.append(String.format(ptn, t));
		return sb;
	}

	public static <T> StringBuilder joinBy(String ptn, char c, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T t : o)
			sb.append(String.format(ptn, t)).append(c);
		sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder joinBy(char c, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T t : o)
			sb.append(t.toString()).append(c);
		sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder join(T... o) {
		return joinBy(',', o);
	}
}
