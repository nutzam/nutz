package org.nutz.lang.random;

import java.util.Random;

public class R {
	private R() {}

	static Random r = new Random();

	/**
	 * Returns a pseudorandom, uniformly distributed int value between min
	 * (inclusive) and the max (inclusive)
	 */
	public static int random(int min, int max) {
		return r.nextInt(max - min + 1) + min;
	}

	public static StringGenerator sg(int min, int max) {
		return new StringGenerator(min, max);
	}

	public static StringGenerator sg(int len) {
		return new StringGenerator(len, len);
	}
}
