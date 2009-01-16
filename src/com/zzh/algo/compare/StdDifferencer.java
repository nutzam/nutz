package com.zzh.algo.compare;

import java.util.ArrayList;

/**
 * <b>Differencer class:</b> <hr color=grey size=1>
 * 
 * An implement of {@link IDifferencer}. The algorithm used is an objectified
 * version of one described in: <it>A File Comparison Program,</it> by Webb
 * Miller and Eugene W. Myers, Software Practice and Experience, Vol. 15, Nov.
 * 1985.
 * 
 * @author thao created @ 2006
 * @see IDifferencer.
 */
public class StdDifferencer implements IDifferencer {
	private static final Difference[] EMPTY_RESULT = new Difference[0];

	public Difference[] compare(IComparator left, IComparator right) {
		// Get the M and N, the end point is (M,N)
		int m = left.getCount();
		int n = right.getCount();

		// count the Max of D.
		int max = m + n;
		// init the V array.
		int[] v = new int[max * 2 + 1];
		// edit script corresponding to v[k]
		LinkedDifference[] script = new LinkedDifference[max * 2 + 1];
		int x, y;

		// find common string of D = 0
		for (x = 0; x < m && x < n && itemEqual(left, x, right, x); x++)
			;
		// vk used to mapping -MAX to MAX. so vk = max instead of k = 0.Actually
		// k = x - y.
		int vk = max;
		v[vk] = x;
		script[vk] = null;
		// determine wheather or not two Comparators are complite the same.
		if (x >= m && x >= n) {
			return EMPTY_RESULT;
		}

		LinkedDifference edit = null;
		boolean inRange = true;
		// For D <- 1 to MAX Do
		for (int d = 1; d <= max; d++) {
			// For k <- -D to D in steps of 2 Do
			for (int k = -d; k <= d; k += 2) {
				vk = k + max;
				// If k = -D or k != D and V[k-1]<V[k+1]
				if (k == -d || k != d && v[vk - 1] <= v[vk + 1]) {
					// x <- V[k+1]
					x = v[vk + 1];
					// y <- x-k
					y = x - k;
					if (inRange = (x <= m && y <= n)) {
						edit = new LinkedDifference(script[vk + 1],
								LinkedDifference.INSERT);
					}
				} else {
					// x <- V[k-1]+1
					x = v[vk - 1] + 1;
					// y <- x-k
					y = x - k;
					if (inRange = (x <= m && y <= n)) {
						edit = new LinkedDifference(script[vk - 1],
								LinkedDifference.DELETE);
					}

				}
				if (inRange) {
					// index from 0
					edit.mIndex = x - 1;
					edit.nIndex = y - 1;
					script[vk] = edit;
				}
				// While x<M and y<N and a[x+1]=b[y+1] Do (x,y)<-(x+1,y+1)
				while (x < m && y < n && itemEqual(left, x, right, y)) {
					++x;
					++y;
				}
				// V[k] <- x
				v[vk] = x;
				// If x >= M and y >= N THEN Stop
				if (x == m && y == n) {
					return createDifferences(script[vk]);
				}
			}
		}
		// should not be here
		return null;
	}

	public Difference[] compareSmart(IComparator left, IComparator right,
			int step) {
		// Get the M and N, the end point is (M,N)
		int m = left.getCount();
		int n = right.getCount();
		int min = Math.min(m, n);
		int i = 0;
		LinkedDifference difference = null;
		while (i + step <= min) {
			difference = compare(left, i, step, right, i, step, difference);
			i += step;
		}
		// comprare teh rest
		difference = compare(left, i, m - i, right, i, n - i, difference);
		return createDifferences(difference);
	}

	private LinkedDifference compare(IComparator left, int leftStart,
			int leftLength, IComparator right, int rightStart, int rightLength,
			LinkedDifference root) {
		// Get the M and N, the end point is (M,N)
		int m = leftLength;
		int n = rightLength;

		// count the Max of D.
		int max = m + n;
		// init the V array.
		int[] v = new int[max * 2 + 1];
		// edit script corresponding to v[k]
		LinkedDifference[] script = new LinkedDifference[max * 2 + 1];
		int x, y;

		// find common string of D = 0
		for (x = 0; x < m && x < n
				&& itemEqual(left, x + leftStart, right, x + rightStart); x++)
			;
		// vk used to mapping -MAX to MAX. so vk = max instead of k = 0.Actually
		// k = x - y.
		int vk = max;
		v[vk] = x;
		script[vk] = root;
		// determine wheather or not two Comparators are complite the same.
		if (x >= m && x >= n) {
			return root;
		}

		LinkedDifference edit = null;
		boolean inRange = true;
		// For D <- 1 to MAX Do
		for (int d = 1; d <= max; d++) {
			// For k <- -D to D in steps of 2 Do
			for (int k = -d; k <= d; k += 2) {
				vk = k + max;
				// If k = -D or k != D and V[k-1]<V[k+1]
				if (k == -d || k != d && v[vk - 1] <= v[vk + 1]) {
					// x <- V[k+1]
					x = v[vk + 1];
					// y <- x-k
					y = x - k;
					if (inRange = (x <= m && y <= n)) {
						edit = new LinkedDifference(script[vk + 1],
								LinkedDifference.INSERT);
					}
				} else {
					// x <- V[k-1]+1
					x = v[vk - 1] + 1;
					// y <- x-k
					y = x - k;
					if (inRange = (x <= m && y <= n)) {
						edit = new LinkedDifference(script[vk - 1],
								LinkedDifference.DELETE);
					}

				}
				if (inRange) {
					// index from 0
					edit.mIndex = x - 1 + leftStart;
					edit.nIndex = y - 1 + rightStart;
					script[vk] = edit;
				}
				// While x<M and y<N and a[x+1]=b[y+1] Do (x,y)<-(x+1,y+1)
				while (x < m
						&& y < n
						&& itemEqual(left, x + leftStart, right, y + rightStart)) {
					++x;
					++y;
				}
				// V[k] <- x
				v[vk] = x;
				// If x >= M and y >= N THEN Stop
				if (x == m && y == n) {
					return script[vk];
				}
			}
		}
		// should not be here
		return null;
	}

	private Difference[] createDifferences(LinkedDifference difference) {
		LinkedDifference ep = reverseDifferences(difference);
		ArrayList<Difference> result = new ArrayList<Difference>();
		Difference diff = null;

		while (ep != null) {
			diff = new Difference();
			diff.leftStart = ep.mIndex;
			diff.rightStart = ep.nIndex;
			if (ep.kind == LinkedDifference.INSERT) {
				diff.rightLength++;
			} else if (ep.kind == LinkedDifference.DELETE) {
				diff.leftLength++;
			}
			boolean next = true;
			while (next) {
				ep = ep.next;
				next = appendDiff(diff, ep);
			}
			result.add(diff);
		}
		return (Difference[]) result.toArray(EMPTY_RESULT);
	}

	private boolean appendDiff(Difference diff, LinkedDifference ep) {
		boolean result = true;
		if (ep == null) {
			result = false;
		} else {
			int leftNextPosition = diff.leftStart + diff.leftLength;
			int rightNextPosition = diff.rightStart + diff.rightLength;
			leftNextPosition = leftNextPosition < 0 ? 0 : leftNextPosition;
			rightNextPosition = rightNextPosition < 0 ? 0 : rightNextPosition;
			if (ep.kind == LinkedDifference.DELETE
					&& ep.mIndex == leftNextPosition
					&& ep.nIndex <= rightNextPosition) {
				diff.leftStart = diff.leftStart < 0 ? 0 : diff.leftStart;
				diff.leftLength++;
			} else if (ep.kind == LinkedDifference.INSERT
					&& ep.mIndex <= leftNextPosition
					&& ep.nIndex == rightNextPosition) {
				diff.rightStart = diff.rightStart < 0 ? 0 : diff.rightStart;
				diff.rightLength++;
			} else {
				result = false;
			}
		}
		return result;
	}

	private LinkedDifference reverseDifferences(LinkedDifference start) {
		LinkedDifference ep, behind, ahead;

		ahead = start;
		ep = null;
		while (ahead != null) {
			behind = ep;
			ep = ahead;
			ahead = ahead.getNext();
			ep.setNext(behind);
		}
		return ep;
	}

	private boolean itemEqual(IComparator left, int leftIndex,
			IComparator right, int rightIndex) {
		if (leftIndex >= left.getCount() || rightIndex >= right.getCount()) {
			return false;
		} else {
			return left.itemEqual(leftIndex, right, rightIndex);
		}
	}

	private static class LinkedDifference {
		static final int INSERT = 0;
		static final int DELETE = 1;

		int mIndex;
		int nIndex;
		LinkedDifference next;
		int kind;

		/*
		 * Creates a LinkedDifference
		 */
		LinkedDifference() {
			next = null;
		}

		/*
		 * Constructs and links a LinkeDifference to another LinkedDifference
		 */
		LinkedDifference(LinkedDifference next, int operation) {
			this.kind = operation;
			this.next = next;
		}

		/*
		 * Follows the next link
		 */
		LinkedDifference getNext() {
			return next;
		}

		boolean isDelete() {
			return this.kind == DELETE;
		}

		boolean isInsert() {
			return this.kind == INSERT;
		}

		/*
		 * Sets the next link of this LinkedDifference
		 */
		void setNext(LinkedDifference next) {
			this.next = next;
		}
	}
}
