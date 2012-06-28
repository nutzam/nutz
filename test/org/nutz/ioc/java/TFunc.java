package org.nutz.ioc.java;

import org.nutz.lang.random.R;

public class TFunc {

    public static String getAbc() {
        return "ABC";
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
