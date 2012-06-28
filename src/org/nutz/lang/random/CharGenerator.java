package org.nutz.lang.random;

/**
 * Generted one char
 * 
 * @author zozoh
 * @author wendal(wendal1985@gmail.com)
 */
public class CharGenerator {
    private CharGenerator() {}
    
    private static final char[] src = "1234567890_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static char next() {
        return src[Math.abs(R.r.nextInt(src.length))];
    }
}
