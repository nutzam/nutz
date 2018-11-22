package org.nutz.lang.util;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class IntSet {

    public static IntSet make(String s) {
        if (s.length() < 3)
            throw Lang.makeThrow("Invalid IntSet : '%s'", s);
        s = Strings.trim(s);
        char l = s.charAt(0);
        char r = s.charAt(s.length() - 1);
        IntRange ir = IntRange.make(s.substring(1, s.length() - 1));

        return new IntSet(l, r, ir);
    }

    private char l;
    private char r;
    private IntRange ir;

    private IntSet(char l, char r, IntRange ir) {
        this.l = l;
        this.r = r;
        this.ir = ir;
    }

    public boolean match(int n) {
        // [left : right]
        if (l == '[' && r == ']') {
            return ir.inon(n);
        }
        // (left : right)
        else if (l == '(' && r == ')') {
            return ir.in(n);
        }
        // [left : right)
        else if (l == '[') {
            return ir.linon(n);
        }
        // (left : right]
        return ir.rinon(n);
    }

    @Override
    public String toString() {
        return new StringBuilder().append(l).append(ir.toString()).append(r).toString();
    }
    
    

}
