package org.nutz.lang.random;

/**
 * Generted one char
 * 
 * @author zozoh
 * 
 */
public class CharGenerator {
	private static char[] src = "1234567890_ABCDEFGHI GKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz"
			.toCharArray();

	public static char next() {
		return src[Math.abs(GM.r.nextInt(src.length))];
	}
}
