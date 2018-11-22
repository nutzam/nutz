package org.nutz.mvc.upload.util;

import org.nutz.lang.Lang;

/**
 * 根据给给定的 bytes[] 计算所有的查找回溯点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class RemountBytes {
    
    public static RemountBytes create(String str){
        return create(Lang.toBytes(str.toCharArray()));
    }

    public static RemountBytes create(byte[] bs) {
        // 初始化失效数组
        int[] fails = new int[bs.length];

        // 如果数组长度大于 0
        if (bs.length > 1) {
            // 如果字符为 ABABCDE
            // 循环从第三个开始的字符
            for (int i = 2; i < bs.length; i++) {
                // 如果当前为 C
                // red: 本字符之前的子串 ABAB
                // int redL = 0;
                // int redR = i - 1;
                // blue: 本字符之前的子串不包括第一个字符的子串 BAB
                int blueL = 1;
                int blueR = i - 1;
                // 循环，遇到 blue 结束，不相等的话，游标置零
                int x = 0;
                for (int j = blueL; j <= blueR; j++) {
                    byte red = bs[x];
                    byte blue = bs[j];
                    if (red == blue)
                        x++;
                    else
                        x = 0;
                }
                // 当 blue 全部耗尽，长度为失效数组的值
                fails[i] = x;
            }
        }
        RemountBytes re = new RemountBytes();
        re.bytes = bs;
        re.fails = fails;
        return re;
    }

    public byte[] bytes;

    public int[] fails;

}
