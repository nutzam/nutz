package org.nutz.lang;

import java.io.PrintStream;
import java.util.Locale;

public class Printer {

	public static void print(boolean b) {
		System.out.print(b);
	}

	public static void print(char c) {
		System.out.print(c);
	}

	public static void print(char[] s) {
		System.out.print(s);
	}

	public static void print(double d) {
		System.out.print(d);
	}

	public static void print(float f) {
		System.out.print(f);
	}

	public static void print(int i) {
		System.out.print(i);
	}

	public static void print(long l) {
		System.out.print(l);
	}

	public static void print(Object obj) {
		System.out.print(obj);
	}

	public static void print(String s) {
		System.out.print(s);
	}

	public static PrintStream printf(Locale l, String format, Object... args) {
		return System.out.printf(l, format, args);
	}

	public static PrintStream printf(String format, Object... args) {
		return System.out.printf(format, args);
	}

	public static void println() {
		System.out.println();
	}

	public static void println(boolean x) {
		System.out.println(x);
	}

	public static void println(char x) {
		System.out.println(x);
	}

	public static void println(char[] x) {
		System.out.println(x);
	}

	public static void println(double x) {
		System.out.println(x);
	}

	public static void println(float x) {
		System.out.println(x);
	}

	public static void println(int x) {
		System.out.println(x);
	}

	public static void println(long x) {
		System.out.println(x);
	}

	public static void println(Object x) {
		System.out.println(x);
	}

	public static void println(String x) {
		System.out.println(x);
	}

}
