package org.nutz.lang;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 获得字符数组的全排列
     * 
     * @param arr
     * @return
     */
    public static String[] permutation(char... arr) {
        return permutation(arr.length, arr);
    }

    /**
     * 按照指定长度, 获得字符数组的全排列
     * 
     * @param arr
     * @return
     */
    public static String[] permutation(int length, char... arr) {
        if (arr == null || arr.length == 0 || length <= 0 || length > arr.length) {
            return null;
        }
        List<String> slist = new ArrayList<String>();
        char[] b = new char[length]; // 辅助空间，保存待输出组合数
        getCombination(slist, arr, length, 0, b, 0);
        return slist.toArray(new String[]{});
    }

    // --------------------------- 以下为几个辅助方法

    private static void getCombination(List<String> slist,
                                       char[] a,
                                       int n,
                                       int begin,
                                       char[] b,
                                       int index) {
        if (n == 0) {// 如果够n个数了，输出b数组
            getAllPermutation(slist, b, 0);// 得到b的全排列
            return;
        }
        for (int i = begin; i < a.length; i++) {
            b[index] = a[i];
            getCombination(slist, a, n - 1, i + 1, b, index + 1);
        }

    }

    private static void getAllPermutation(List<String> slist, char[] a, int index) {
        /* 与a的元素个数相同则输出 */
        if (index == a.length - 1) {
            slist.add(String.valueOf(a));
            return;
        }
        for (int i = index; i < a.length; i++) {
            swap(a, index, i);
            getAllPermutation(slist, a, index + 1);
            swap(a, index, i);
        }
    }

    private static void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
