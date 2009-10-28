package org.nutz.lang;

import java.util.LinkedList;

/**
 * 提供了一组方便字符串操作的便利函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Strings {

	public static String dup(CharSequence cs, int num) {
		if (cs == null || num <= 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++)
			sb.append(cs);
		return sb.toString();
	}

	public static String dup(char c, int num) {
		StringBuilder sb = new StringBuilder(c);
		for (int i = 0; i < num; i++)
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
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(s.charAt(0))).append(s.subSequence(1, s.length()));
		return sb.toString();
	}

	/**
	 * 检查两个字符串的忽略大小写后是否相等.
	 * <p/><b>当s1 == null && s2 == null, 本方法返回false<b/>
	 * @param s1 字符串A
	 * @param s2 字符串B
	 * @return true 如果两个字符串忽略大小写后相等,且两个字符串均不为null
	 */
	public static boolean equalsIgnoreCase(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		return s1.equalsIgnoreCase(s2);
	}

	/**
	 * 检查两个字符串是否相等.
	 * <p/><b>当s1 == null && s2 == null, 本方法返回false<b/>
	 * @param s1 字符串A
	 * @param s2 字符串B
	 * @return true 如果两个字符串相等,且两个字符串均不为null
	 */
	public static boolean equals(String s1, String s2) {
		if (s1 == null || s2 == null)
			return false;
		return s1.equals(s2);
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

	public static String trim(CharSequence cs) {
		if (null == cs)
			return null;
		if (cs.length() == 0)
			return cs.toString();
		int l = 0;
		int last = cs.length() - 1;
		int r = last;
		for (; l < cs.length(); l++) {
			char c = cs.charAt(l);
			if (c > 0x20 || c < 0)
				break;
		}
		for (; r > 0; r--) {
			char c = cs.charAt(r);
			if (c > 0x20 || c < 0)
				break;
		}
		if (l > r)
			return "";
		else if (l == 0 && r == last)
			return cs.toString();

		if (cs instanceof String)
			return ((String) cs).substring(l, r + 1);
		return cs.subSequence(l, r + 1).toString();
	}

	public static String[] splitIgnoreBlank(String s) {
		return Strings.splitIgnoreBlank(s, ",");
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

	public static String fillDigit(int d, int width) {
		return Strings.alignRight(String.valueOf(d), width, '0');
	}

	public static String fillHex(int d, int width) {
		return Strings.alignRight(Integer.toHexString(d), width, '0');
	}

	public static String fillBinary(int d, int width) {
		return Strings.alignRight(Integer.toBinaryString(d), width, '0');
	}

	public static String alignRight(CharSequence cs, int width, char c) {
		if (null == cs)
			return null;
		if (cs.length() >= width)
			return cs.toString();
		StringBuilder sb = new StringBuilder();
		int max = width - cs.length();
		for (int i = 0; i < max; i++)
			sb.append(c);
		sb.append(cs);
		return sb.toString();
	}

	public static String alignLeft(CharSequence cs, int width, char c) {
		if (null == cs)
			return null;
		if (cs.length() >= width)
			return cs.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(cs);
		int max = width - cs.length();
		for (int i = 0; i < max; i++)
			sb.append(c);
		return sb.toString();
	}

	public static boolean isQuoteByIgnoreBlank(CharSequence cs, char lc, char rc) {
		if (null == cs)
			return false;
		if (cs.length() < 2)
			return false;
		// check left
		int l = 0;
		int last = cs.length() - 1;
		int r = last;
		for (; l < cs.length(); l++) {
			char c = cs.charAt(l);
			if (c > 0x20 || c < 0)
				break;
		}
		for (; r > 0; r--) {
			char c = cs.charAt(r);
			if (c > 0x20 || c < 0)
				break;
		}
		if (l >= r)
			return false;
		else if (cs.charAt(l) != lc)
			return false;
		else if (cs.charAt(r) != rc)
			return false;
		return true;
	}

	public static boolean isQuoteBy(CharSequence cs, char l, char r) {
		if (null == cs)
			return false;
		if (cs.length() < 2)
			return false;
		if (cs.charAt(0) != l)
			return false;
		if (cs.charAt(cs.length() - 1) != r)
			return false;
		return true;
	}
}
