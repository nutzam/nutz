package org.nutz.lang.random;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.nutz.lang.Strings;

/**
 * 对随机数操作的封装
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class R {

    static Random r = new Random();

    /**
     * 根据一个范围，生成一个随机的整数
     * 
     * @param min
     *            最小值（包括）
     * @param max
     *            最大值（包括）
     * @return 随机数
     */
    public static int random(int min, int max) {
        return r.nextInt(max - min + 1) + min;
    }

    /**
     * 根据一个长度范围，生成一个随机的字符串，字符串内容为 [0-9a-zA-Z_]
     * 
     * @param min
     *            最小值（包括）
     * @param max
     *            最大值（包括）
     * @return 随机字符串
     */

    public static StringGenerator sg(int min, int max) {
        return new StringGenerator(min, max);
    }

    /**
     * 生成一个确定长度的随机字符串，字符串内容为 [0-9a-zA-Z_]
     * 
     * @param len
     *            字符串长度
     * @return 随机字符串
     */
    public static StringGenerator sg(int len) {
        return new StringGenerator(len, len);
    }

    private static final char[] _UU64 = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * @return 64进制表示的紧凑格式的 UUID
     * @see org.nutz.lang.random.R#UU64(UUID)
     */
    public static String UU64() {
        return UU64(UUID.randomUUID());
    }

    /**
     * 返回一个 UUID ，并用 64 进制转换成紧凑形式的字符串，内容为 [\\-0-9a-zA-Z_]
     * <p>
     * 比如一个类似下面的 UUID:
     * 
     * <pre>
     * a6c5c51c-689c-4525-9bcd-c14c1e107c80
     * 一共 128 位，分做L64 和 R64，分为为两个 64位数（两个 long）
     *    > L = uu.getLeastSignificantBits();
     *    > R = uu.getMostSignificantBits();
     * 而一个 64 进制数，是 6 位，因此我们取值的顺序是
     * 1. 从L64位取10次，每次取6位
     * 2. 从L64位取最后的4位 ＋ R64位头2位拼上
     * 3. 从R64位取10次，每次取6位
     * 4. 剩下的两位最后取
     * 这样，就能用一个 22 长度的字符串表示一个 32 长度的UUID，压缩了 1/3
     * </pre>
     * 
     * @param uu
     *            UUID 对象
     * @return 64进制表示的紧凑格式的 UUID
     */
    public static String UU64(UUID uu) {
        int index = 0;
        char[] cs = new char[22];
        long L = uu.getMostSignificantBits();
        long R = uu.getLeastSignificantBits();
        long mask = 63;
        // 从L64位取10次，每次取6位
        for (int off = 58; off >= 4; off -= 6) {
            long hex = (L & (mask << off)) >>> off;
            cs[index++] = _UU64[(int) hex];
        }
        // 从L64位取最后的4位 ＋ R64位头2位拼上
        int l = (int) (((L & 0xF) << 2) | ((R & (3 << 62)) >>> 62));
        cs[index++] = _UU64[l];
        // 从R64位取10次，每次取6位
        for (int off = 56; off >= 2; off -= 6) {
            long hex = (R & (mask << off)) >>> off;
            cs[index++] = _UU64[(int) hex];
        }
        // 剩下的两位最后取
        cs[index++] = _UU64[(int) (R & 3)];
        // 返回字符串
        return new String(cs);
    }

    /**
     * 从一个 UU64 恢复回一个 UUID 对象
     * 
     * @param uu64
     *            64进制表示的 UUID, 内容为 [\\-0-9a-zA-Z_]
     * @return UUID 对象
     */
    public static UUID fromUU64(String uu64) {
        String uu16 = UU16FromUU64(uu64);
        return UUID.fromString(UU(uu16));
    }

    /**
     * 将紧凑格式的 UU16 字符串变成标准 UUID 格式的字符串
     * 
     * @param uu16
     * @return 标准 UUID 字符串
     */
    public static String UU(String uu16) {
        StringBuilder sb = new StringBuilder();
        sb.append(uu16.substring(0, 8));
        sb.append('-');
        sb.append(uu16.substring(8, 12));
        sb.append('-');
        sb.append(uu16.substring(12, 16));
        sb.append('-');
        sb.append(uu16.substring(16, 20));
        sb.append('-');
        sb.append(uu16.substring(20));
        return sb.toString();
    }

    private static final char[] _UU16 = "0123456789abcdef".toCharArray();

    /**
     * 将一个 UU64 表示的紧凑字符串，变成 UU16 表示的字符串
     * 
     * <pre>
     * 每次取2个字符，恢复成3个byte，重复10次，
     * 最后一次，是用最后2个字符，恢复回2个byte 
     * </prev>
     * 
     * @param uu64
     *            uu64 64进制表示的 UUID, 内容为 [\\-0-9a-zA-Z_]
     * @return 16进制表示的紧凑格式的 UUID
     */
    public static String UU16FromUU64(String uu64) {
        byte[] bytes = new byte[32];
        char[] cs = uu64.toCharArray();
        int index = 0;
        // 每次取2个字符，恢复成3个byte，重复10次，
        for (int i = 0; i < 10; i++) {
            int off = i * 2;
            char cl = cs[off];
            char cr = cs[off + 1];
            int l = Arrays.binarySearch(_UU64, cl);
            int r = Arrays.binarySearch(_UU64, cr);
            int n = (l << 6) | r;
            bytes[index++] = (byte) ((n & 0xF00) >>> 8);
            bytes[index++] = (byte) ((n & 0xF0) >>> 4);
            bytes[index++] = (byte) (n & 0xF);
        }
        // 最后一次，是用最后2个字符，恢复回2个byte
        char cl = cs[20];
        char cr = cs[21];
        int l = Arrays.binarySearch(_UU64, cl);
        int r = Arrays.binarySearch(_UU64, cr);
        int n = (l << 2) | r;
        bytes[index++] = (byte) ((n & 0xF0) >>> 4);
        bytes[index++] = (byte) (n & 0xF);

        // 返回 UUID 对象
        char[] names = new char[32];
        for (int i = 0; i < bytes.length; i++)
            names[i] = _UU16[bytes[i]];
        return new String(names);
    }

    /**
     * @return 16进制表示的紧凑格式的 UUID
     */
    public static String UU16() {
        return UU16(UUID.randomUUID());
    }

    /**
     * @param uu
     *            UUID 对象
     * @return 16进制表示的紧凑格式的 UUID
     */
    public static String UU16(UUID uu) {
        return Strings.alignRight(Long.toHexString(uu.getMostSignificantBits()), 16, '0')
                + Strings.alignRight(Long.toHexString(uu.getLeastSignificantBits()), 16, '0');
    }
}
