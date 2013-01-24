package org.nutz.lang;

/**
 * 关于数的一些帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Nums {

    /**
     * @param is
     *            变参
     * @return 数组
     */
    public static int[] array(int... is) {
        return is;
    }

    /**
     * 判断一个整数是否在数组中
     * 
     * @param ary
     *            数组
     * @param i
     *            整数
     * @return 是否存在
     */
    public static boolean isin(int[] ary, int i) {
        if (null != ary)
            for (int num : ary)
                if (num == i)
                    return true;
        return false;
    }

    /**
     * 整合两个整数数组为一个数组
     * <b>这个方法在JDK5不可用!!<b/>
     * @param ary
     *            整数数组
     * @param is
     *            变参
     * @return 新的整合过的数组
     */
    public static int[] join(int[] ary, int... is) {
        if (null == ary)
            return is;
        int length = ary.length + is.length;
        int[] re = new int[length];
        System.arraycopy(ary, 0, re, 0, ary.length);
        int i = ary.length;
        for (int num : is)
            re[i++] = num;
        return re;
    }
}
