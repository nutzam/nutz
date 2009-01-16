package com.zzh.algo.compare;

/**
 * <b>IDifferencer class:</b> <hr color=grey size=1>
 * 
 * @see IComparator,Difference
 * @author thao created @ 2006
 */
public interface IDifferencer {
	/**
	 * Compare two Comparators.
	 * 
	 * @param left
	 * @param right
	 * @return array of <code>Difference</code>, if they are compelete the same,
	 *         the array length is 0.
	 */
	public Difference[] compare(IComparator left, IComparator right);
}
