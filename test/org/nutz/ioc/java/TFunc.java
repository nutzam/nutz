package org.nutz.ioc.java;

public class TFunc {

	public static String getAbc() {
		return "ABC";
	}

	public static String checkCase(boolean flag, String s) {
		if (null == s)
			return null;
		if (flag)
			return s.toUpperCase();
		return s.toLowerCase();
	}

}
