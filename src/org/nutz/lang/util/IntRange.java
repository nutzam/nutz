package org.nutz.lang.util;

import org.nutz.lang.Strings;

public class IntRange {

    public static IntRange make(String s) {
        char[] cs = Strings.trim(s).toCharArray();
        int i = 0;
        for (; i < cs.length; i++) {
            char c = cs[i];
            if (c == ',' || c == ':')
                break;
        }
        if (i == cs.length)
            return make(Integer.parseInt(new String(cs)));

        int left = Integer.parseInt(String.valueOf(cs, 0, i));
        return make(left, Integer.parseInt(String.valueOf(cs, ++i, cs.length - i)));
    }

    public static IntRange make(int right) {
        return make(0, right);
    }

    public static IntRange make(int left, int right) {
        return new IntRange(left, right);
    }

    private int left;
    private int right;

    private IntRange(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public boolean in(int n) {
        return n > left && n < right;
    }

    public boolean on(int n) {
        return n == left || n == right;
    }

    public boolean inon(int n) {
        return on(n) || in(n);
    }

    public boolean gt(int n) {
        return n < left;
    }

    public boolean lt(int n) {
        return n > right;
    }

    /**
     * @param n
     * @return n >= left && n < right;
     */
    public boolean linon(int n) {
        return n >= left && n < right;
    }

    /**
     * @param n
     * @return n > left && n <= right;
     */
    public boolean rinon(int n) {
        return n > left && n <= right;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String toString() {
        return String.format("%d:%d", left, right);
    }
}
