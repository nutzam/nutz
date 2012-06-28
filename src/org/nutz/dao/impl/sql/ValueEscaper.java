package org.nutz.dao.impl.sql;

import org.nutz.lang.util.LinkedArray;
import org.nutz.lang.util.LinkedCharArray;

/**
 * 通过 add 函数，可以增加需要逃逸的字符，以及如何逃逸
 * <p>
 * 然后调用 escape 函数，执行逃逸
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class ValueEscaper {

    public ValueEscaper() {
        this.charStack = new LinkedCharArray(20);
        this.escapeStack = new LinkedArray<String>(20);
    }

    private LinkedCharArray charStack;
    private LinkedArray<String> escapeStack;
    private char[] chars;
    private String[] escapes;

    /**
     * 设定逃逸方式
     * 
     * @param c
     *            要逃逸的字符
     * @param s
     *            如何逃逸
     * @return 自身
     */
    public ValueEscaper add(char c, String s) {
        charStack.push(c);
        escapeStack.push(s);
        return this;
    }

    /**
     * 在执行 escape 前，这个函数一定要调用，它会把你增加的逃逸设置，初始化一下
     * 
     * @return 自身
     */
    public ValueEscaper ready() {
        chars = charStack.toArray();
        escapes = escapeStack.toArray();
        return this;
    }

    /**
     * 根据逃逸的设置，对传入的字符串进行逃逸
     * 
     * @param cs
     *            字符序列
     * @return 逃逸后的字符序列
     */
    public CharSequence escape(CharSequence cs) {
        StringBuilder sb = new StringBuilder();
        boolean find;
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            find = false;
            for (int x = 0; x < chars.length; x++) {
                if (c == chars[x]) {
                    sb.append(escapes[x]);
                    find = true;
                    break;
                }
            }
            if (!find)
                sb.append(c);
        }
        return sb;
    }

}
