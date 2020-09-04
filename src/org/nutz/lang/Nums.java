package org.nutz.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.util.Regex;

/**
 * 关于数的一些帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Nums {

    /**
     * 对齐浮点数精度，超过精度四舍五入
     * 
     * @param n
     *            浮点数
     * @param dp
     *            精度
     * @return 对齐精度的浮点数
     */
    public static float precision(float n, int dp) {
        if (0 == dp) {
            return Math.round(n);
        }
        if (1 == dp) {
            return Math.round(n * 10f) / 10f;
        }
        if (2 == dp) {
            return Math.round(n * 100f) / 100f;
        }
        if (3 == dp) {
            return Math.round(n * 1000f) / 1000f;
        }
        // 其他精度
        float p = (float) Math.pow(10, dp);
        return Math.round(n * p) / p;
    }

    /**
     * 对齐双精度浮点精度，超过精度四舍五入
     * 
     * @param n
     *            双精度浮点数
     * @param dp
     *            精度
     * @return 对齐精度的双精度浮点数
     */
    public static double precision(double n, int dp) {
        if (0 == dp) {
            return Math.round(n);
        }
        if (1 == dp) {
            return Math.round(n * 10) / 10.0;
        }
        if (2 == dp) {
            return Math.round(n * 100) / 100.0;
        }
        if (3 == dp) {
            return Math.round(n * 1000) / 1000.0;
        }
        // 其他精度
        double p = Math.pow(10, dp);
        return Math.round(n * p) / p;
    }

    /**
     * @param a
     *            数字
     * @param b
     *            数字
     * @return 两个数的最大公约数 <code>greatest common divisor(gcd)</code>
     */
    public static int gcd(int a, int b) {
        a = Math.round(a);
        b = Math.round(b);
        if (b != 0) {
            return gcd(b, a % b);
        }
        return a;
    }

    /**
     * @param list
     *            一组整数
     * @return 一组整数的最大公约数 <code>greatest common divisor(gcd)</code>
     */
    public static int gcds(int... list) {
        // 没数
        if (list.length == 0)
            return Integer.MIN_VALUE;
        // 一个是自己
        if (list.length == 1) {
            return list[0];
        }
        // 两个以上
        int gcd = gcd(list[0], list[1]);
        for (int i = 2; i < list.length; i++) {
            gcd = gcd(gcd, list[i]);
        }
        // 返回
        return gcd;
    }

    /**
     * @param a
     *            数字
     * @param b
     *            数字
     * @return 两个数的最小公倍数 <code>lowest common multiple (LCM)</code>
     */
    public static int lcm(int a, int b) {
        a = Math.round(a);
        b = Math.round(b);
        return a * b / gcd(a, b);
    }

    /**
     * @param list
     *            一组整数
     * @return 一组整数的最小公倍数 <code>lowest common multiple (LCM)</code>
     */
    public static int lcms(int... list) {
        // 没数
        if (list.length == 0)
            return Integer.MAX_VALUE;
        // 一个是自己
        if (list.length == 1) {
            return list[0];
        }
        // 两个以上
        int lcm = lcm(list[0], list[1]);
        for (int i = 2; i < list.length; i++) {
            lcm = lcm(lcm, list[i]);
        }
        // 返回
        return lcm;
    }

    /**
     * 计算尺寸
     * 
     * @param v
     *            要计算的尺寸值的类型可以是
     *            <ul>
     *            <li>500 - 整数，直接返回
     *            <li>.12 - 浮点，相当于一个百分比，可以大于 1.0
     *            <li>"12%" - 百分比，相当于 .12
     *            </ul>
     * @param base
     *            百分比的基数
     * 
     * @return 根据基数计算后的数值
     */
    public static double dimension(String v, double base) {
        // 试试整型
        try {
            Integer nb = Integer.valueOf(v);
            return nb.intValue();
        }
        catch (NumberFormatException e) {}

        // 试试浮点
        try {
            Double nb = Double.valueOf(v);
            return nb.doubleValue() * base;
        }
        catch (NumberFormatException e) {}

        // 百分比
        Pattern p = Regex.getPattern("^([0-9.]{1,})%$");
        Matcher m = p.matcher(v);
        if (m.find()) {
            Double nb = Double.valueOf(m.group(1));
            return (nb.doubleValue() / 100) * base;
        }
        // 靠不知道是啥
        throw Lang.makeThrow("fail to dimension : " + v);
    }

    /**
     * @see #dimension(String, double)
     */
    public static int dimension(String v, int base) {
        return (int) (dimension(v, (double) base));
    }

    /**
     * @param nbs
     *            一组数字
     * @return 数字之和
     */
    public static int sum(int... nbs) {
        int re = 0;
        for (int nb : nbs)
            re += nb;
        return re;
    }

    /**
     * 一个数的字面量的进制和值
     */
    public static class Radix {
        Radix(String val, int radix) {
            this.val = val;
            this.radix = radix;
        }

        public int radix;
        public String val;
    }

    /**
     * @param str
     *            数字的字符串
     * @return 字符串的进制
     * 
     * @see org.nutz.lang.Nums.Radix
     */
    public static Radix evalRadix(String str) {
        if (str.startsWith("0x"))
            return new Radix(str.substring(2), 16);
        if (str.startsWith("0") && str.length() > 1)
            return new Radix(str.substring(1), 8);
        if (str.startsWith("0b"))
            return new Radix(str.substring(2), 2);
        return new Radix(str, 10);
    }

    /**
     * 将一个字符串变成一个整型数组，如果字符串不符合规则，对应的元素为 -1 <br>
     * 比如：
     * 
     * <pre>
     * "3,4,9"   =>  [  3, 4, 9 ]
     * "a,9,100" =>  [ -1, 9, 100 ]
     * </pre>
     * 
     * @param str
     *            半角逗号分隔的数字字符串
     * @return 数组
     */
    public static int[] splitInt(String str) {
        String[] ss = Strings.splitIgnoreBlank(str);
        if (null == ss)
            return null;
        int[] ns = new int[ss.length];
        for (int i = 0; i < ns.length; i++) {
            try {
                ns[i] = Integer.parseInt(ss[i]);
            }
            catch (NumberFormatException e) {
                ns[i] = -1;
            }
        }
        return ns;
    }

    /**
     * @see #splitInt(String)
     */
    public static long[] splitLong(String str) {
        String[] ss = Strings.splitIgnoreBlank(str);
        if (null == ss)
            return null;
        long[] ns = new long[ss.length];
        for (int i = 0; i < ns.length; i++) {
            try {
                ns[i] = Long.parseLong(ss[i]);
            }
            catch (NumberFormatException e) {
                ns[i] = -1;
            }
        }
        return ns;
    }

    /**
     * 将一个字符串变成一个浮点数数组，如果字符串不符合规则，对应的元素为 0.0 <br>
     * 比如：
     * 
     * <pre>
     * "3,4,9"     =>  [ 3.0f, 4.0f, 9.0f ]
     * "a,9.8,100" =>  [ 0.0f, 9.0f, 100.0f ]
     * </pre>
     * 
     * @param str
     *            半角逗号分隔的数字字符串
     * @return 数组
     */
    public static float[] splitFloat(String str) {
        String[] ss = Strings.splitIgnoreBlank(str);
        if (null == ss)
            return null;
        float[] ns = new float[ss.length];
        for (int i = 0; i < ns.length; i++) {
            try {
                ns[i] = Float.parseFloat(ss[i]);
            }
            catch (NumberFormatException e) {
                ns[i] = 0.0f;
            }
        }
        return ns;
    }

    /**
     * 将一个字符串变成一个双精度数数组，如果字符串不符合规则，对应的元素为 -1
     * 
     * @param str
     *            半角逗号分隔的数字字符串
     * @return 数组
     */
    public static double[] splitDouble(String str) {
        String[] ss = Strings.splitIgnoreBlank(str);
        if (null == ss)
            return null;
        double[] ns = new double[ss.length];
        for (int i = 0; i < ns.length; i++) {
            try {
                ns[i] = Long.parseLong(ss[i]);
            }
            catch (NumberFormatException e) {
                ns[i] = -1;
            }
        }
        return ns;
    }

    /**
     * @see #splitInt(String)
     */
    public static boolean[] splitBoolean(String str) {
        String[] ss = Strings.splitIgnoreBlank(str);
        if (null == ss)
            return null;
        boolean[] ns = new boolean[ss.length];
        for (int i = 0; i < ns.length; i++) {
            try {
                ns[i] = Regex.match("^(1|yes|true|on)$", ss[i].toLowerCase());
            }
            catch (NumberFormatException e) {
                ns[i] = false;
            }
        }
        return ns;
    }

    /**
     * @see #indexOf(int[], int, int)
     */
    public static int indexOf(int[] arr, int v) {
        return indexOf(arr, v, 0);
    }

    /**
     * @param arr
     *            数组
     * @param v
     *            值
     * @param off
     *            从那个下标开始搜索(包含)
     * @return 第一个匹配元素的下标
     */
    public static int indexOf(int[] arr, int v, int off) {
        if (null != arr)
            for (int i = off; i < arr.length; i++) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * @param arr
     * @param v
     * @return 最后一个匹配元素的下标
     */
    public static int lastIndexOf(int[] arr, int v) {
        if (null != arr)
            for (int i = arr.length - 1; i >= 0; i--) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * @see #indexOf(char[], char, int)
     */
    public static int indexOf(char[] arr, char v) {
        if (null != arr)
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * @param arr
     *            数组
     * @param v
     *            值
     * @param off
     *            从那个下标开始搜索(包含)
     * @return 第一个匹配元素的下标
     */
    public static int indexOf(char[] arr, char v, int off) {
        if (null != arr)
            for (int i = off; i < arr.length; i++) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * @param arr
     * @param v
     * @return 第一个匹配元素的下标
     */
    public static int lastIndexOf(char[] arr, char v) {
        if (null != arr)
            for (int i = arr.length - 1; i >= 0; i--) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * @see #indexOf(long[], long, int)
     */
    public static int indexOf(long[] arr, long v) {
        return indexOf(arr, v, 0);
    }

    /**
     * @param arr
     *            数组
     * @param v
     *            值
     * @param off
     *            从那个下标开始搜索(包含)
     * @return 第一个匹配元素的下标
     */
    public static int indexOf(long[] arr, long v, int off) {
        if (null != arr)
            for (int i = off; i < arr.length; i++) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * @param arr
     * @param v
     * @return 第一个匹配元素的下标
     */
    public static int lastIndexOf(long[] arr, long v) {
        if (null != arr)
            for (int i = arr.length - 1; i >= 0; i--) {
                if (arr[i] == v)
                    return i;
            }
        return -1;
    }

    /**
     * 不解释，你懂的
     */
    public static int[] array(int... is) {
        return is;
    }

    /**
     * 判断一个整数是否在数组中
     * 
     * @param arr
     *            数组
     * @param i
     *            整数
     * @return 是否存在
     */
    public static boolean isin(int[] arr, int i) {
        return indexOf(arr, i) >= 0;
    }

    /**
     * 整合两个整数数组为一个数组 <b>这个方法在JDK5不可用!!<b/>
     * 
     * @param arr
     *            整数数组
     * @param is
     *            变参
     * @return 新的整合过的数组
     */
    public static int[] join(int[] arr, int... is) {
        if (null == arr)
            return is;
        int length = arr.length + is.length;
        int[] re = new int[length];
        System.arraycopy(arr, 0, re, 0, arr.length);
        int i = arr.length;
        for (int num : is)
            re[i++] = num;
        return re;
    }

    /**
     * 不解释，你懂的
     */
    public static long[] arrayL(long... is) {
        return is;
    }

    /**
     * 判断一个长整数是否在数组中
     * 
     * @param arr
     *            数组
     * @param i
     *            长整数
     * @return 是否存在
     */
    public static boolean isin(long[] arr, long i) {
        return indexOf(arr, i) >= 0;
    }

    /**
     * 整合两个长整数数组为一个数组 <b>这个方法在JDK5不可用!!<b/>
     * 
     * @param arr
     *            长整数数组
     * @param is
     *            变参
     * @return 新的整合过的数组
     */
    public static long[] join(long[] arr, long... is) {
        if (null == arr)
            return is;
        int length = arr.length + is.length;
        long[] re = new long[length];
        System.arraycopy(arr, 0, re, 0, arr.length);
        int i = arr.length;
        for (long num : is)
            re[i++] = num;
        return re;
    }

    /**
     * 不解释，你懂的
     */
    public static char[] arrayC(char... is) {
        return is;
    }

    /**
     * 判断一个长整数是否在数组中
     * 
     * @param arr
     *            数组
     * @param i
     *            长整数
     * @return 是否存在
     */
    public static boolean isin(char[] arr, char i) {
        return indexOf(arr, i) >= 0;
    }

    /**
     * 整合两个长整数数组为一个数组 <b>这个方法在JDK5不可用!!<b/>
     * 
     * @param arr
     *            长整数数组
     * @param is
     *            变参
     * @return 新的整合过的数组
     */
    public static char[] join(char[] arr, char... is) {
        if (null == arr)
            return is;
        int length = arr.length + is.length;
        char[] re = new char[length];
        System.arraycopy(arr, 0, re, 0, arr.length);
        int i = arr.length;
        for (char num : is)
            re[i++] = num;
        return re;
    }
}
