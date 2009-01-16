package com.zzh.algo.compare;

/**
 * <b>IComparator Interface:</b> <hr color=grey size=1> A container of items
 * which are used to compare.
 * 
 * @see IDifferencer
 * @author thao created @ 2006
 */
public interface IComparator {
	/**
	 * Get the count of comparator's items.
	 * 
	 * @return the count of items.
	 */
	public int getCount();

	/**
	 * Compare two comparators' items specified by index.
	 * 
	 * @param index
	 *            the index of the comparator's items.
	 * @param other
	 *            other comparator used to compare.
	 * @param otherIndex
	 *            the index of the other comparator's items.
	 * @return true if the two items are equal, otherwise return false.
	 */
	public boolean itemEqual(int index, IComparator other, int otherIndex);

	/**
	 * Get the item specified by index.
	 * 
	 * @param index
	 *            the index of the comparator's items.
	 * @return The specified item or null If index is illegal.
	 */
	public Object getItem(int index);
}
