package com.zzh.lang;

import java.util.LinkedList;

public class Strings {

	public static CharSequence dup(CharSequence cs, int num) {
		if (cs == null || num <= 0)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++)
			sb.append(cs);
		return sb.toString();
	}

	public static CharSequence dup(char c, int num) {
		StringBuffer sb = new StringBuffer(c);
		for (int i = 1; i < num; i++)
			sb.append(c);
		return sb.toString();
	}

	public static String capitalize(CharSequence s) {
		if (null == s)
			return null;
		if (s.length() == 0)
			return "";
		if (Character.isUpperCase(s.charAt(0)))
			return s.toString();
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(s.charAt(0))).append(s.subSequence(1, s.length()));
		return sb.toString();
	}

	public static boolean equalsIgnoreCase(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		return s1.equalsIgnoreCase(s2);
	}

	public static boolean isEmpty(CharSequence cs) {
		if (null == cs)
			return true;
		return cs.length() == 0;
	}

	public static boolean isBlank(CharSequence s) {
		if (null == s)
			return true;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 0x20 || c < 0)
				return false;
		}
		return true;
	}

	public static String trim(CharSequence s) {
		if (null == s)
			return null;
		if (s.length() == 0)
			return s.toString();
		if (s.length() == 1) {
			char c = s.charAt(0);
			if (c <= 0x20 && c >= 0)
				return "";
			return s.toString();
		}
		int start = 0;
		int lastIndex = s.length() - 1;
		int end = lastIndex;
		for (; start < s.length(); start++) {
			char c = s.charAt(start);
			if (c > 0x20 || c < 0)
				break;
		}
		for (; end > 0; end--) {
			char c = s.charAt(end);
			if (c > 0x20 || c < 0)
				break;
		}
		if (start > end)
			return "";
		else if (start == 0 && end == lastIndex)
			return s.toString();

		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= end; i++)
			sb.append(s.charAt(i));

		return sb.toString();
	}

	public static String[] splitIgnoreBlank(String s) {
		return Strings.splitIgnoreBlank(s, "[,]");
	}

	public static String[] splitIgnoreBlank(String s, String regex) {
		if (null == s)
			return null;
		String[] ss = s.split(regex);
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < ss.length; i++) {
			if (isBlank(ss[i]))
				continue;
			list.add(trim(ss[i]));
		}
		String[] re = new String[list.size()];
		list.toArray(re);
		return re;
	}

	public static CharSequence fillDigit(int d, int width) {
		return Strings.alignRight(String.valueOf(d), width, '0');
	}

	public static CharSequence fillHex(int d, int width) {
		return Strings.alignRight(Integer.toHexString(d), width, '0');
	}

	public static CharSequence alignRight(CharSequence cs, int width, char c) {
		if (null == cs)
			return null;
		if (cs.length() >= width)
			return cs;
		StringBuffer sb = new StringBuffer();
		int max = width - cs.length();
		for (int i = 0; i < max; i++)
			sb.append(c);
		sb.append(cs);
		return sb;
	}

	public static CharSequence alignLeft(CharSequence cs, int width, char c) {
		if (null == cs)
			return null;
		if (cs.length() >= width)
			return cs;
		StringBuffer sb = new StringBuffer();
		sb.append(cs);
		int max = width - cs.length();
		for (int i = 0; i < max; i++)
			sb.append(c);
		return sb;
	}
}
