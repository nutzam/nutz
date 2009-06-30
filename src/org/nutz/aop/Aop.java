package org.nutz.aop;

import org.nutz.lang.Strings;

import static java.lang.reflect.Modifier.*;

public class Aop {

	public static void main(String[] args) throws Exception {
		pbin(PUBLIC | PROTECTED | PRIVATE | STATIC | TRANSIENT);
	}

	public static boolean isMask(int mod, int mask) {
		return 0 == ~((~mask) | mod);
	}

	public static void pbin(int i) {
		System.out.println(bin(i));
	}

	public static String bin(int i) {
		return Strings.alignRight(Integer.toBinaryString(i), 32, '.');
	}

	public static MethodMatcher matcher() {
		return matcher(-1);
	}

	public static MethodMatcher matcher(int mod) {
		return matcher(null, null, mod);
	}

	public static MethodMatcher matcher(String regex) {
		return matcher(regex, 0);
	}

	public static MethodMatcher matcher(String regex, int mod) {
		return matcher(regex, null, mod);
	}

	public static MethodMatcher matcher(String regex, String ignore, int mod) {
		return new MethodMatcher(regex, ignore, mod);
	}
}
