package org.nutz.lang.util;

import org.nutz.lang.Strings;

public class IntRange {

	public static IntRange make(String s) {
		return make(Strings.trim(s).toCharArray());
	}

	public static IntRange make(char[] cs) {
		int i = 0;
		for (; i < cs.length; i++) {
			char c = cs[i];
			if (c == ',' || c == ':')
				break;
		}
		if (i == 0)
			return make(Integer.parseInt(new String(cs)));
		int l = Integer.parseInt(String.valueOf(cs, 0, i));
		if (i == cs.length)
			return make(l, l);
		return make(l, Integer.parseInt(String.valueOf(cs, ++i, cs.length - i)));

	}

	public static IntRange make(int right) {
		return make(0, right);
	}

	public static IntRange make(int left, int right) {
		return new IntRange(left, right);
	}

	private int left;
	private int right;

	private IntRange(int left, int right) {
		this.left = left;
		this.right = right;
	}

	public boolean in(int i) {
		return i > left && i < right;
	}

	public boolean on(int i) {
		return i == left || i == right;
	}

	public boolean inon(int i) {
		return on(i) || in(i);
	}

	public boolean gt(int i) {
		return i < left;
	}

	public boolean lt(int i) {
		return i > right;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public String toString() {
		return String.format("%d,%d", left, right);
	}
}
