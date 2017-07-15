package org.nutz.lang.util;

import org.nutz.castor.Castors;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * 描述了一个区间
 * <ul>
 * <li>T 对象必须实现有意义的 toString，并且字符串中不能包含半角逗号
 * <li>T 对象必须可比较
 * <li>T 对象必须可以被 Castors 正确的从字符串转换
 * <li>T 对象的 toString() 和 Castors 的转换必须可逆
 * </ul>
 * 
 * 任何区间的字符串描述都符合:
 * 
 * <pre>
 * 全封闭的区间 : [T0, T1]
 * 左开右闭区间 : (T0, T1]
 * 左闭右开区间 : [T0, T1)
 * 左开右闭区间 : (T0, T1]
 * 全开放的区间 : (T0, T1)
 * 精确等于某值 : (T0] 或 [T0) 或 [T0]
 * 精确不等于某值 : (T0)
 * </pre>
 * 
 * 比如对于数字:
 * 
 * <pre>
 * [4,10]   // >=4 && <=10
 * (6,54]   // >=6 && <54
 * (,78)    // <78
 * [50]     // == 50
 * (99)     // !=99
 * </pre>
 * 
 * 对于日期
 * 
 * <pre>
 * [2012-09-10 12:33:24, 2013-08-14]   // 会自动交换大小值，可以是日期或者时间
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Region<T extends Comparable<T>> {

    public static IntRegion Int(String str) {
        return new IntRegion(str);
    }

    public static IntRegion Intf(String fmt, Object... args) {
        return new IntRegion(String.format(fmt, args));
    }

    public static LongRegion Long(String str) {
        return new LongRegion(str);
    }

    public static LongRegion Longf(String fmt, Object... args) {
        return new LongRegion(String.format(fmt, args));
    }

    public static FloatRegion Float(String str) {
        return new FloatRegion(str);
    }

    public static FloatRegion Floatf(String fmt, Object... args) {
        return new FloatRegion(String.format(fmt, args));
    }

    public static DoubleRegion Double(String str) {
        return new DoubleRegion(str);
    }

    public static DoubleRegion Doublef(String fmt, Object... args) {
        return new DoubleRegion(String.format(fmt, args));
    }

    public static DateRegion Date(String str) {
        return new DateRegion(str);
    }

    public static DateRegion Datef(String fmt, Object... args) {
        return new DateRegion(String.format(fmt, args));
    }

    protected Class<T> eleType;

    protected T left;

    protected T right;

    protected boolean leftOpen;

    protected boolean rightOpen;

    public T left() {
        return left;
    }

    public Region<T> left(T left) {
        this.left = left;
        return this;
    }

    public Region<T> leftOpen(boolean open) {
        this.leftOpen = open;
        return this;
    }

    public T right() {
        return right;
    }

    public Region<T> right(T right) {
        this.right = right;
        return this;
    }

    public Region<T> rightOpen(boolean open) {
        this.rightOpen = open;
        return this;
    }

    public boolean isLeftOpen() {
        return leftOpen;
    }

    public boolean isRightOpen() {
        return rightOpen;
    }

    /**
     * @return 是区间还是一个精确匹配的值
     */
    public boolean isRegion() {
        return left != right && !isNull();
    }

    /**
     * @return 当前区间是否为空
     */
    public boolean isNull() {
        return null == left && null == right;
    }

    /**
     * 根据左边开闭区间的情况返回一个正确的左边比较的运算符
     * 
     * @param gt
     *            大于的运算符，开区间时采用
     * @param gte
     *            大于等于的运算符，闭区间时采用
     * @return 运算符
     */
    public String leftOpt(String gt, String gte) {
        if (null == left)
            return null;
        return leftOpen ? gt : gte;
    }

    /**
     * 根据右边开闭区间的情况返回一个正确的右边比较的运算符
     * 
     * @param lt
     *            小于的运算符，开区间时采用
     * @param lte
     *            小于等于的运算符，闭区间时采用
     * @return 运算符
     */
    public String rightOpt(String lt, String lte) {
        if (null == right)
            return null;
        return rightOpen ? lt : lte;
    }

    /**
     * @param obj
     *            对象
     * @return 对象是否在这个区间
     */
    public boolean match(T obj) {
        if (null == obj)
            return false;
        if (!isRegion()) {
            // 左右都是开区间，表示不等于
            if (this.leftOpen && this.rightOpen) {
                return left.compareTo(obj) != 0;
            }
            return left.compareTo(obj) == 0;
        }
        if (null != left) {
            int c = obj.compareTo(left);
            if (c < 0 || c == 0 && leftOpen) {
                return false;
            }
        }
        if (null != right) {
            int c = obj.compareTo(right);
            if (c > 0 || c == 0 && rightOpen) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Region() {
        eleType = (Class<T>) (Class) Mirror.getTypeParam(getClass(), 0);
    }

    /**
     * 解析
     * 
     * @param str
     *            字符串
     * @return 自身
     */
    public Region<T> valueOf(String str) {
        String s2 = Strings.trim(str.substring(1, str.length() - 1));
        leftOpen = str.charAt(0) == '(';
        rightOpen = str.charAt(str.length() - 1) == ')';

        // 只有左值
        if (s2.endsWith(",")) {
            left = fromString(Strings.trim(s2.substring(0, s2.length() - 1)));
            right = null;
        }
        // 只有右值
        else if (s2.startsWith(",")) {
            left = null;
            right = fromString(Strings.trim(s2.substring(1)));
        }
        // 两侧都有值
        else {
            String[] ss = Strings.splitIgnoreBlank(s2, ",");
            // 精确的值
            if (1 == ss.length) {
                left = fromString(ss[0]);
                right = left;
            }
            // 一个区间
            else {
                left = fromString(ss[0]);
                right = fromString(ss[1]);
                // 看看是否需要交换交换...
                if (null != left && null != right && left.compareTo(right) > 0) {
                    T o = right;
                    right = left;
                    left = o;
                }
            }
        }
        return this;
    }

    public String toString(T obj) {
        return null == obj ? "" : obj.toString();
    }

    public T fromString(String str) {
        str = Strings.trim(str);
        if (Strings.isEmpty(str))
            return null;
        return Castors.me().castTo(str, eleType);
    }

    public String toString() {
        if (this.isRegion())
            return String.format("%c%s,%s%c",
                                 leftOpen ? '(' : '[',
                                 toString(left),
                                 toString(right),
                                 rightOpen ? ')' : ']');

        return String.format("%c%s%c", leftOpen ? '(' : '[', toString(left), rightOpen ? ')' : ']');
    }

}
