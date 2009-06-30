package org.nutz.lang.random;

public class StringGenerator {

	public StringGenerator(int max) {
		maxLen = max;
		minLen = 1;
	}

	public StringGenerator(int min, int max) {
		maxLen = max;
		minLen = min;
	}

	/**
	 * min length of the string
	 */
	private int maxLen = -1;

	/**
	 * max length of the string
	 */
	private int minLen = -1;

	public void setup(int max, int min) {
		maxLen = max;
		minLen = min;
	}

	public String next() {
		if (maxLen <= 0 || minLen <= 0)
			return null;
		char[] buf = new char[GM.random(minLen, maxLen)];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = CharGenerator.next();
		}
		return new String(buf);
	}

}
