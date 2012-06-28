package org.nutz.el.issue125;

public class StringUtil {
    public static String test(String[] names) {
        StringBuffer sb = new StringBuffer();
        for(String name : names){
            sb.append(name);
        }
        return sb.toString();
    }
}