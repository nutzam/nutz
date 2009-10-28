package org.nutz.lang;

public abstract class Maths {

	public static int bit(String s) {
		return Integer.valueOf(s, 2);
	}

	/**
	 * Test current bit is match the given mask at least one bit or not.
	 * 
	 * @param bs
	 *            integer, bit map
	 * @param mask
	 *            another bit map
	 * @return if one of bit value is '1' in mask, and it is also is '1' in bs
	 *         return true, else false
	 */
	public static boolean isMask(int bs, int mask) {
		return 0 != (mask & bs);
	}

	public static boolean isNoMask(int bs, int mask) {
		return 0 == (bs & mask);
	}

	/**
	 * Test current bit is all match the give mask.
	 * 
	 * @param bs
	 *            integer, bit map
	 * @param mask
	 *            another bit map
	 * @return if all bit value is '1' in mask, and it is also is '1' in bs
	 *         return true, else false
	 */
	public static boolean isMaskAll(int bs, int mask) {
		return 0 == ~((~mask) | bs);
	}

	/**
	 * Get part of one integer as a new integer
	 * 
	 * @param bs
	 *            original integer
	 * @param low
	 *            the low bit position (inclusive), 0 base
	 * @param hight
	 *            the hight bit position (exclusive), 0 base
	 * @return
	 */
	public static int extract(int bs, int low, int high) {
		bs = bs >> low;
		int mask = 0;
		for (int i = 0; i < (high - low); i++) {
			mask += 1 << i;
		}
		return bs &= mask;
	}

	/**
	 * 利用二分法，在字符数组中查找某一个字符的索引值。 假设给定的字符数组是从小到大排序过的，并且没有重复字符
	 * 
	 * @param cs
	 *            字符数组
	 * @param c
	 *            目标字符
	 * @return 目标字符在字符数组的索引
	 */
	public static int find(char[] cs, char c) {
		// 如果数组长度小于 5，直接循环更快
		if (cs.length < 5) {
			for (int i = 0; i < cs.length; i++)
				if (cs[i] == c)
					return i;
		}
		// 数组元素较多，则使用二分法。数组必须是依次递增，左小右大
		else {
			int left = 0;
			int right = cs.length - 1;
			while (left < right) {
				// 获取中间值
				int m = (left + right) / 2;
				if (cs[m] == c)
					return m;
				// 目标字符应该在中点右侧
				if (cs[m] < c) {
					left = m + 1;
				}
				// 目标字符应该在中点左侧侧
				else {
					right = m - 1;
				}
			}
			if (cs[left] == c)
				return left;
		}
		return -1;
	}

	/**
	 * 参看 find(char[],char) 函数的说明。一样的意思
	 */
	public static int find(int[] is, int n) {
		// 如果数组长度小于 5，直接循环更快
		if (is.length < 5) {
			for (int i = 0; i < is.length; i++)
				if (is[i] == n)
					return i;
		}
		// 数组元素较多，则使用二分法。数组必须是依次递增，左小右大
		else {
			int left = 0;
			int right = is.length - 1;
			while (left < right) {
				// 获取中间值
				int m = (left + right) / 2;
				if (is[m] == n)
					return m;
				// 目标字符应该在中点右侧
				if (is[m] < n) {
					left = m + 1;
				}
				// 目标字符应该在中点左侧侧
				else {
					right = m - 1;
				}
			}
			if (is[left] == n)
				return left;
		}
		return -1;
	}
}
