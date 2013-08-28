package org.nutz.lang.util;

import java.util.Date;

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
 * </pre>
 * 
 * 比如对于数字:
 * 
 * <pre>
 * [4,10]   // >=4 && <=10
 * (6,54]   // >=6 && <54
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

    public static Region<Integer> Int(String str) {
        return new Region<Integer>() {}.valueOf(str);
    }

    public static Region<Long> Long(String str) {
        return new Region<Long>() {}.valueOf(str);
    }

    public static Region<Float> Float(String str) {
        return new Region<Float>() {}.valueOf(str);
    }

    public static Region<Double> Double(String str) {
        return new Region<Double>() {}.valueOf(str);
    }

    public static Region<Date> Date(String str) {
        return new Region<Date>() {}.valueOf(str);
    }

    protected Class<T> eleType;

    protected T left;

    protected T right;

    protected boolean leftIsOpen;

    protected boolean rightIsOpen;

    /**
     * @param obj
     *            对象
     * @return 对象是否在这个区间
     */
    public boolean match(T obj) {
        if (null == obj)
            return false;
        int c = obj.compareTo(left);
        if (c < 0 || c == 0 && leftIsOpen) {
            return false;
        }
        c = obj.compareTo(right);
        if (c > 0 || c == 0 && rightIsOpen) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public Region() {
        eleType = (Class<T>) Mirror.getTypeParam(getClass(), 0);
    }

    /**
     * 解析
     * 
     * @param str
     *            字符串
     * @return 自身
     */
    public Region<T> valueOf(String str) {
        String[] ss = Strings.splitIgnoreBlank(str.substring(1, str.length() - 1), ",");
        leftIsOpen = str.charAt(0) == '(';
        rightIsOpen = str.charAt(str.length() - 1) == ')';
        left = fromString(ss[0]);
        right = fromString(ss[1]);
        // 看看是否需要交换交换...
        if (left.compareTo(right) > 0) {
            T o = right;
            right = left;
            left = o;
        }
        return this;
    }

    public String toString(T obj) {
        return obj.toString();
    }

    public T fromString(String str) {
        return Castors.me().castTo(str, eleType);
    }

}
