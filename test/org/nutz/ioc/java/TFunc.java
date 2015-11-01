package org.nutz.ioc.java;

import org.nutz.lang.random.R;

public class TFunc {

    public static String getAbc() {
        return "ABC";
    }

    public static String dup(String s, int n) {
        if (n == 0)
            return "";
        if (n > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++)
                sb.append(s);
            return sb.toString();
        }
        return s.substring(0, s.length() + n);
    }

    public static String tFloat(String s, float f) {
        return s + ":" + f;
    }

    public static String checkCase(boolean flag, String s) {
        if (null == s)
            return null;
        if (flag)
            return s.toUpperCase();
        return s.toLowerCase();
    }

    public String noStatic() {
        return R.sg(10, 20).next();
    }

    public static final String XNAME = "Wendal";

}
