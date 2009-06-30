package org.nutz.lang.random;

import java.util.Random;

public class GM // GeneratorManager
{
	static Random r = new Random();

	/**
	 * Returns a pseudorandom, uniformly distributed int value between min
	 * (inclusive) and the max (inclusive)
	 */
	public static int random(int min, int max) {
		return r.nextInt(max - min + 1) + min;
	}

}
