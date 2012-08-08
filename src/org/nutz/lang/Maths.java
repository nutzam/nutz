package org.nutz.lang;

/**
 * A group of helper functions to counting some ...
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author pw
 */
public abstract class Maths {

    /**
     * 返回最大的一个
     * 
     * @param nums
     * @return
     */
    public static int max(int... nums) {
        return takeOne(new CompareSomeThing() {
            public boolean compare(int arg0, int arg1) {
                return arg0 > arg1;
            }
        }, nums);
    }

    /**
     * 返回最小的一个
     * 
     * @param nums
     * @return
     */
    public static int min(int... nums) {
        return takeOne(new CompareSomeThing() {
            public boolean compare(int arg0, int arg1) {
                return arg0 < arg1;
            }
        }, nums);
    }

    private interface CompareSomeThing {
        public boolean compare(int arg0, int arg1);
    }

    private static int takeOne(CompareSomeThing cp, int... nums) {
        if (null == nums || nums.length == 0)
            return 0;
        int re = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (cp.compare(nums[i], re))
                re = nums[i];
        }
        return re;
    }

    /**
     * Convert a binary string to a integer
     * 
     * @param s
     *            binary string
     * @return integer
     */
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
     * @param high
     *            the hight bit position (exclusive), 0 base
     * @return new integer
     */
    public static int extract(int bs, int low, int high) {
        bs = bs >> low;
        int mask = 0;
        for (int i = 0; i < (high - low); i++) {
            mask += 1 << i;
        }
        return bs & mask;
    }

}
