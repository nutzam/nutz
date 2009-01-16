package com.zzh.algo.compare;

/**
 * <b>Difference class:</b> <hr color=grey size=1> Discribe a difference between
 * two entities.
 * 
 * @see IDifferencer
 * @author thao created @ 2006
 */
public class Difference {
	public int leftStart;
	public int leftLength;
	public int rightStart;
	public int rightLength;

	/**
	 * @param leftStart
	 *            left start of difference
	 * @param leftLength
	 *            left length of difference
	 * @param rightStart
	 *            right start of difference
	 * @param rightLength
	 *            right length of difference
	 */
	public Difference(int leftStart, int leftLength, int rightStart,
			int rightLength) {
		this.leftStart = leftStart;
		this.leftLength = leftLength;
		this.rightStart = rightStart;
		this.rightLength = rightLength;
	}

	public Difference() {
	}
}
