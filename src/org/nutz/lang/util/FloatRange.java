package org.nutz.lang.util;

import org.nutz.lang.Strings;

public class FloatRange {

    public static FloatRange make(String s) {
        char[] cs = Strings.trim(s).toCharArray();
        int i = 0;
        for (; i < cs.length; i++) {
            char c = cs[i];
            if (c == ',' || c == ':')
                break;
        }
        if (i == cs.length)
            return make(Float.parseFloat(new String(cs)));

        float left = Float.parseFloat(String.valueOf(cs, 0, i));
        
        return make(left, Float.parseFloat(String.valueOf(cs, ++i, cs.length - i)));
    }

    public static FloatRange make(float right) {
        return make(0, right);
    }

    public static FloatRange make(float left, float right) {
        return new FloatRange(left, right);
    }

    private float left;
    private float right;

    private FloatRange(float left, float right) {
        this.left = left;
        this.right = right;
    }

    public boolean in(float n) {
        return n > left && n < right;
    }

    public boolean on(float n) {
        return n == left || n == right;
    }

    public boolean inon(float n) {
        return on(n) || in(n);
    }

    public boolean gt(float n) {
        return n < left;
    }

    public boolean lt(float n) {
        return n > right;
    }

    /**
     * @param n
     * @return n >= left && n < right;
     */
    public boolean linon(float n) {
        return n >= left && n < right;
    }

    /**
     * @param n
     * @return n > left && n <= right;
     */
    public boolean rinon(float n) {
        return n > left && n <= right;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public String toString() {
        return String.format("%s:%s", left, right);
    }
}
