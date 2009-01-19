package com.zzh.lang.random;

import java.util.Random;

public class GM // GeneratorManager
{
	private static Random r = new Random();

	public static Random rnd() {
		if (null == r)
			synchronized (r) {
				if (null == r) {
					r = new Random();
				}
			}
		return r;
	}

	/**
	 * Returns a pseudorandom, uniformly distributed int value between min
	 * (inclusive) and the max (inclusive)
	 */
	public static int gRandom(int min, int max) {
		return GM.rnd().nextInt(max - min + 1) + min;
	}

}
