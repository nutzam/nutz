package org.nutz.lang;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.meta.Email;

/**
 * 字符串操作的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author mawm(ming300@gmail.com)
 * @author bonyfish(mc02cxj@gmail.com)
 * @author pw(pangwu86@gmail.com)
 */
public class Strings {

    protected Strings() {}

    /**
     * 是中文字符吗?
     * 
     * @param c
     *            待判定字符
     * @return 判断结果
     */
    public static boolean isChineseCharacter(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符是否为全角字符
     * 
     * @param c
     *            字符
     * @return 判断结果
     */
    public static boolean isFullWidthCharacter(char c) {
        // 全角空格为12288，半角空格为32
        // 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
        // 全角空格 || 其他全角字符
        if (c == 12288 || (c > 65280 && c < 65375)) {
            return true;
        }
        // 中文全部是全角
        if (isChineseCharacter(c)) {
            return true;
        }
        // 日文判断
        // 全角平假名 u3040 - u309F
        // 全角片假名 u30A0 - u30FF
        if (c >= '\u3040' && c <= '\u30FF') {
            return true;
        }
        return false;
    }

    /**
     * 转换成半角字符
     * 
     * @param c
     *            待转换字符
     * @return 转换后的字符
     */
    public static char toHalfWidthCharacter(char c) {
        if (c == 12288) {
            return (char) 32;
        } else if (c > 65280 && c < 65375) {
            return (char) (c - 65248);
        }
        return c;
    }

    /**
     * 转换为半角字符串
     * 
     * @param str
     *            待转换字符串
     * @return 转换后的字符串
     */
    public static String toHalfWidthString(CharSequence str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(toHalfWidthCharacter(str.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * 判断是否是全角字符串(所有字符都是全角)
     * 
     * @param str
     *            被判断的字符串
     * @return 判断结果
     */
    public static boolean isFullWidthString(CharSequence str) {
        return charLength(str) == str.length() * 2;
    }

    /**
     * 判断是否是半角字符串(所有字符都是半角)
     * 
     * @param str
     *            被判断的字符串
     * @return 判断结果
     */
    public static boolean isHalfWidthString(CharSequence str) {
        return charLength(str) == str.length();
    }

    /**
     * 计算字符串的字符长度(全角算2, 半角算1)
     * 
     * @param str
     *            被计算的字符串
     * @return 字符串的字符长度
     */
    public static int charLength(CharSequence str) {
        int clength = 0;
        for (int i = 0; i < str.length(); i++) {
            clength += isFullWidthCharacter(str.charAt(i)) ? 2 : 1;
        }
        return clength;
    }

    /**
     * 复制字符串
     * 
     * @param cs
     *            字符串
     * @param num
     *            数量
     * @return 新字符串
     */
    public static String dup(CharSequence cs, int num) {
        if (isEmpty(cs) || num <= 0)
            return "";
        StringBuilder sb = new StringBuilder(cs.length() * num);
        for (int i = 0; i < num; i++)
            sb.append(cs);
        return sb.toString();
    }

    /**
     * 复制字符
     * 
     * @param c
     *            字符
     * @param num
     *            数量
     * @return 新字符串
     */
    public static String dup(char c, int num) {
        if (c == 0 || num < 1)
            return "";
        StringBuilder sb = new StringBuilder(num);
        for (int i = 0; i < num; i++)
            sb.append(c);
        return sb.toString();
    }

    /**
     * 将字符串首字母大写
     * 
     * @param s
     *            字符串
     * @return 首字母大写后的新字符串
     * @deprecated 推荐使用 {@link #upperFirst(CharSequence)}
     */
    public static String capitalize(CharSequence s) {
        return upperFirst(s);
    }

    /**
     * 将字符串首字母小写
     * 
     * @param s
     *            字符串
     * @return 首字母小写后的新字符串
     */
    public static String lowerFirst(CharSequence s) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == 0)
            return "";
        char c = s.charAt(0);
        if (Character.isLowerCase(c))
            return s.toString();
        return new StringBuilder(len).append(Character.toLowerCase(c))
                                     .append(s.subSequence(1, len))
                                     .toString();
    }

    /**
     * 将字符串首字母大写
     * 
     * @param s
     *            字符串
     * @return 首字母大写后的新字符串
     */
    public static String upperFirst(CharSequence s) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == 0)
            return "";
        char c = s.charAt(0);
        if (Character.isUpperCase(c))
            return s.toString();
        return new StringBuilder(len).append(Character.toUpperCase(c))
                                     .append(s.subSequence(1, len))
                                     .toString();
    }

    /**
     * 检查两个字符串的忽略大小写后是否相等.
     * 
     * @param s1
     *            字符串A
     * @param s2
     *            字符串B
     * @return true 如果两个字符串忽略大小写后相等,且两个字符串均不为null
     */
    public static boolean equalsIgnoreCase(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * 检查两个字符串是否相等.
     * 
     * @param s1
     *            字符串A
     * @param s2
     *            字符串B
     * @return true 如果两个字符串相等,且两个字符串均不为null
     */
    public static boolean equals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    /**
     * 判断字符串是否以特殊字符开头
     * 
     * @param s
     *            字符串
     * @param c
     *            特殊字符
     * @return 是否以特殊字符开头
     */
    public static boolean startsWithChar(String s, char c) {
        return null != s ? (s.length() == 0 ? false : s.charAt(0) == c) : false;
    }

    /**
     * 判断字符串是否以特殊字符结尾
     * 
     * @param s
     *            字符串
     * @param c
     *            特殊字符
     * @return 是否以特殊字符结尾
     */
    public static boolean endsWithChar(String s, char c) {
        return null != s ? (s.length() == 0 ? false : s.charAt(s.length() - 1) == c) : false;
    }

    /**
     * 如果此字符串为 null 或者为空串（""），则返回 true
     * 
     * @param cs
     *            字符串
     * @return 如果此字符串为 null 或者为空，则返回 true
     */
    public static boolean isEmpty(CharSequence cs) {
        return null == cs || cs.length() == 0;
    }

    /**
     * 如果此字符串为 null 或者全为空白字符，则返回 true
     * 
     * @param cs
     *            字符串
     * @return 如果此字符串为 null 或者全为空白字符，则返回 true
     */
    public static boolean isBlank(CharSequence cs) {
        if (null == cs)
            return true;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!(Character.isWhitespace(cs.charAt(i))))
                return false;
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 去掉字符串前后空白字符。空白字符的定义由Character.isWhitespace来判断
     * 
     * @param cs
     *            字符串
     * @return 去掉了前后空白字符的新字符串
     */
    public static String trim(CharSequence cs) {
        if (null == cs)
            return null;
        int length = cs.length();
        if (length == 0)
            return cs.toString();
        int l = 0;
        int last = length - 1;
        int r = last;
        for (; l < length; l++) {
            if (!Character.isWhitespace(cs.charAt(l)))
                break;
        }
        for (; r > l; r--) {
            if (!Character.isWhitespace(cs.charAt(r)))
                break;
        }
        if (l > r)
            return "";
        else if (l == 0 && r == last)
            return cs.toString();
        return cs.subSequence(l, r + 1).toString();
    }

    /**
     * 将给定字符串，变成 "xxx...xxx" 形式的字符串
     * 
     * @param str
     *            字符串
     * @param len
     *            最大长度
     * @return 紧凑的字符串
     */
    public static String brief(String str, int len) {
        if (Strings.isBlank(str) || (str.length() + 3) <= len)
            return str;
        int w = len / 2;
        int l = str.length();
        return str.substring(0, len - w) + " ... " + str.substring(l - w);
    }

    /**
     * 将字符串按半角逗号，拆分成数组，空元素将被忽略
     * 
     * @param s
     *            字符串
     * @return 字符串数组
     */
    public static String[] splitIgnoreBlank(String s) {
        return Strings.splitIgnoreBlank(s, ",");
    }

    /**
     * 根据一个正则式，将字符串拆分成数组，空元素将被忽略
     * 
     * @param s
     *            字符串
     * @param regex
     *            正则式
     * @return 字符串数组
     */
    public static String[] splitIgnoreBlank(String s, String regex) {
        if (null == s)
            return null;
        String[] ss = s.split(regex);
        List<String> list = new LinkedList<String>();
        for (String st : ss) {
            if (isBlank(st))
                continue;
            list.add(trim(st));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 将一个整数转换成最小长度为某一固定数值的十进制形式字符串
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String fillDigit(int d, int width) {
        return Strings.alignRight(String.valueOf(d), width, '0');
    }

    /**
     * 将一个整数转换成最小长度为某一固定数值的十六进制形式字符串
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String fillHex(int d, int width) {
        return Strings.alignRight(Integer.toHexString(d), width, '0');
    }

    /**
     * 将一个整数转换成最小长度为某一固定数值的二进制形式字符串
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String fillBinary(int d, int width) {
        return Strings.alignRight(Integer.toBinaryString(d), width, '0');
    }

    /**
     * 将一个整数转换成固定长度的十进制形式字符串
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String toDigit(int d, int width) {
        return Strings.cutRight(String.valueOf(d), width, '0');
    }

    /**
     * 将一个整数转换成固定长度的十六进制形式字符串
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String toHex(int d, int width) {
        return Strings.cutRight(Integer.toHexString(d), width, '0');
    }

    /**
     * 将一个整数转换成固定长度的二进制形式字符串
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String toBinary(int d, int width) {
        return Strings.cutRight(Integer.toBinaryString(d), width, '0');
    }

    /**
     * 保证字符串为一固定长度。超过长度，切除左侧字符，否则左侧填补字符。
     * 
     * @param s
     *            字符串
     * @param width
     *            长度
     * @param c
     *            补字符
     * @return 修饰后的字符串
     */
    public static String cutRight(String s, int width, char c) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == width)
            return s;
        if (len < width)
            return Strings.dup(c, width - len) + s;
        return s.substring(len - width, len);
    }

    /**
     * 保证字符串为一固定长度。超过长度，切除右侧字符，否则右侧填补字符。
     * 
     * @param s
     *            字符串
     * @param width
     *            长度
     * @param c
     *            补字符
     * @return 修饰后的字符串
     */
    public static String cutLeft(String s, int width, char c) {
        if (null == s)
            return null;
        int len = s.length();
        if (len == width)
            return s;
        if (len < width)
            return s + Strings.dup(c, width - len);
        return s.substring(0, width);
    }

    /**
     * 在字符串左侧填充一定数量的特殊字符
     * 
     * @param o
     *            可被 toString 的对象
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignRight(Object o, int width, char c) {
        if (null == o)
            return null;
        String s = o.toString();
        int len = s.length();
        if (len >= width)
            return s;
        return new StringBuilder().append(dup(c, width - len)).append(s).toString();
    }

    /**
     * 在字符串右侧填充一定数量的特殊字符
     * 
     * @param o
     *            可被 toString 的对象
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignLeft(Object o, int width, char c) {
        if (null == o)
            return null;
        String s = o.toString();
        int length = s.length();
        if (length >= width)
            return s;
        return new StringBuilder().append(s).append(dup(c, width - length)).toString();
    }

    /**
     * 测试此字符串是否被指定的左字符和右字符所包裹；如果该字符串左右两边有空白的时候，会首先忽略这些空白
     * 
     * @param cs
     *            字符串
     * @param lc
     *            左字符
     * @param rc
     *            右字符
     * @return 字符串是被左字符和右字符包裹
     */
    public static boolean isQuoteByIgnoreBlank(CharSequence cs, char lc, char rc) {
        if (null == cs)
            return false;
        int len = cs.length();
        if (len < 2)
            return false;
        int l = 0;
        int last = len - 1;
        int r = last;
        for (; l < len; l++) {
            if (!Character.isWhitespace(cs.charAt(l)))
                break;
        }
        if (cs.charAt(l) != lc)
            return false;
        for (; r > l; r--) {
            if (!Character.isWhitespace(cs.charAt(r)))
                break;
        }
        return l < r && cs.charAt(r) == rc;
    }

    /**
     * 测试此字符串是否被指定的左字符和右字符所包裹
     * 
     * @param cs
     *            字符串
     * @param lc
     *            左字符
     * @param rc
     *            右字符
     * @return 字符串是被左字符和右字符包裹
     */
    public static boolean isQuoteBy(CharSequence cs, char lc, char rc) {
        if (null == cs)
            return false;
        int length = cs.length();
        return length > 1 && cs.charAt(0) == lc && cs.charAt(length - 1) == rc;
    }

    /**
     * 测试此字符串是否被指定的左字符串和右字符串所包裹
     * 
     * @param str
     *            字符串
     * @param l
     *            左字符串
     * @param r
     *            右字符串
     * @return 字符串是被左字符串和右字符串包裹
     */
    public static boolean isQuoteBy(String str, String l, String r) {
        if (null == str || null == l || null == r)
            return false;
        return str.startsWith(l) && str.endsWith(r);
    }

    /**
     * 获得一个字符串集合中，最长串的长度
     * 
     * @param coll
     *            字符串集合
     * @return 最大长度
     */
    public static int maxLength(Collection<? extends CharSequence> coll) {
        int re = 0;
        if (null != coll)
            for (CharSequence s : coll)
                if (null != s)
                    re = Math.max(re, s.length());
        return re;
    }

    /**
     * 获得一个字符串数组中，最长串的长度
     * 
     * @param array
     *            字符串数组
     * @return 最大长度
     */
    public static <T extends CharSequence> int maxLength(T[] array) {
        int re = 0;
        if (null != array)
            for (CharSequence s : array)
                if (null != s)
                    re = Math.max(re, s.length());
        return re;
    }

    /**
     * 对指定对象进行 toString 操作；如果该对象为 null ，则返回空串（""）
     * 
     * @param obj
     *            指定的对象
     * @return 对指定对象进行 toString 操作；如果该对象为 null ，则返回空串（""）
     */
    public static String sNull(Object obj) {
        return sNull(obj, "");
    }

    /**
     * 对指定对象进行 toString 操作；如果该对象为 null ，则返回默认值
     * 
     * @param obj
     *            指定的对象
     * @param def
     *            默认值
     * @return 对指定对象进行 toString 操作；如果该对象为 null ，则返回默认值
     */
    public static String sNull(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }

    /**
     * 对指定对象进行 toString 操作；如果该对象为 null ，则返回空串（""）
     * 
     * @param obj
     *            指定的对象
     * @return 对指定对象进行 toString 操作；如果该对象为 null ，则返回空串（""）
     */
    public static String sBlank(Object obj) {
        return sBlank(obj, "");
    }

    /**
     * 对指定对象进行 toString 操作；如果该对象为 null 或者 toString 方法为空串（""），则返回默认值
     * 
     * @param obj
     *            指定的对象
     * @param def
     *            默认值
     * @return 对指定对象进行 toString 操作；如果该对象为 null 或者 toString 方法为空串（""），则返回默认值
     */
    public static String sBlank(Object obj, String def) {
        if (null == obj)
            return def;
        String s = obj.toString();
        return Strings.isBlank(s) ? def : s;
    }

    /**
     * 截去第一个字符
     * <p>
     * 比如:
     * <ul>
     * <li>removeFirst("12345") => 2345
     * <li>removeFirst("A") => ""
     * </ul>
     * 
     * @param str
     *            字符串
     * @return 新字符串
     */
    public static String removeFirst(CharSequence str) {
        if (str == null)
            return null;
        if (str.length() > 1)
            return str.subSequence(1, str.length()).toString();
        return "";
    }

    /**
     * 如果str中第一个字符和 c一致,则删除,否则返回 str
     * <p>
     * 比如:
     * <ul>
     * <li>removeFirst("12345",1) => "2345"
     * <li>removeFirst("ABC",'B') => "ABC"
     * <li>removeFirst("A",'B') => "A"
     * <li>removeFirst("A",'A') => ""
     * </ul>
     * 
     * @param str
     *            字符串
     * @param c
     *            第一个个要被截取的字符
     * @return 新字符串
     */
    public static String removeFirst(String str, char c) {
        return (Strings.isEmpty(str) || c != str.charAt(0)) ? str : str.substring(1);
    }

    /**
     * 判断一个字符串数组是否包括某一字符串
     * 
     * @param ss
     *            字符串数组
     * @param s
     *            字符串
     * @return 是否包含
     */
    public static boolean isin(String[] ss, String s) {
        if (null == ss || ss.length == 0 || Strings.isBlank(s))
            return false;
        for (String w : ss)
            if (s.equals(w))
                return true;
        return false;
    }

    /**
     * 检查一个字符串是否为合法的电子邮件地址
     * 
     * @param input
     *            需要检查的字符串
     * @return true 如果是有效的邮箱地址
     */
    public static final boolean isEmail(CharSequence input) {
        if (Strings.isBlank(input))
            return false;
        try {
            new Email(input.toString());
            return true;
        }
        catch (Exception e) {}
        return false;
    }

    /**
     * 将一个字符串由驼峰式命名变成分割符分隔单词
     * 
     * <pre>
     *  lowerWord("helloWorld", '-') => "hello-world"
     * </pre>
     * 
     * @param cs
     *            字符串
     * @param c
     *            分隔符
     * 
     * @return 转换后字符串
     */
    public static String lowerWord(CharSequence cs, char c) {
        StringBuilder sb = new StringBuilder();
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            char ch = cs.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0)
                    sb.append(c);
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 将一个字符串某一个字符后面的字母变成大写，比如
     * 
     * <pre>
     *  upperWord("hello-world", '-') => "helloWorld"
     * </pre>
     * 
     * @param cs
     *            字符串
     * @param c
     *            分隔符
     * 
     * @return 转换后字符串
     */
    public static String upperWord(CharSequence cs, char c) {
        StringBuilder sb = new StringBuilder();
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            char ch = cs.charAt(i);
            if (ch == c) {
                do {
                    i++;
                    if (i >= len)
                        return sb.toString();
                    ch = cs.charAt(i);
                } while (ch == c);
                sb.append(Character.toUpperCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 将一个字符串出现的HMTL元素进行转义，比如
     * 
     * <pre>
     *  escapeHtml("&lt;script&gt;alert("hello world");&lt;/script&gt;") => "&amp;lt;script&amp;gt;alert(&amp;quot;hello world&amp;quot;);&amp;lt;/script&amp;gt;"
     * </pre>
     * 
     * 转义字符对应如下
     * <ul>
     * <li>& => &amp;amp;
     * <li>< => &amp;lt;
     * <li>>=> &amp;gt;
     * <li>' => &amp;#x27;
     * <li>" => &amp;quot;
     * </ul>
     * 
     * @param cs
     *            字符串
     * 
     * @return 转换后字符串
     */
    public static String escapeHtml(CharSequence cs) {
        if (null == cs)
            return null;
        char[] cas = cs.toString().toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : cas) {
            switch (c) {
            case '&':
                sb.append("&amp;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\'':
                sb.append("&#x27;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 使用 UTF-8 编码将字符串编码为 byte 序列，并将结果存储到新的 byte 数组
     * 
     * @param cs
     *            字符串
     * 
     * @return UTF-8编码后的 byte 数组
     */
    public static byte[] getBytesUTF8(CharSequence cs) {
        try {
            return cs.toString().getBytes(Encoding.UTF8);
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
    }

    // ####### 几个常用的color相关的字符串转换放这里 ########

    /**
     * 将数字转为十六进制字符串, 默认要使用2个字符(暂时没考虑负数)
     * 
     * @param n
     *            数字
     * @return 十六进制字符串
     */
    public static String num2hex(int n) {
        String s = Integer.toHexString(n);
        return n <= 15 ? "0" + s : s;
    }

    /**
     * 十六进制字符串转换为数字
     * 
     * @param hex
     *            十六进制字符串
     * @return 十进制数字
     */
    public static int hex2num(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * 使用给定的分隔符, 将一个数组拼接成字符串
     * 
     * @param sp
     *            分隔符
     * @param array
     *            要拼接的数组
     * @return 拼接好的字符串
     */
    public static <T> String join2(String sp, T[] array) {
        return Lang.concat(sp, array).toString();
    }

    /**
     * 使用给定的分隔符, 将一个数组拼接成字符串
     * 
     * @param sp
     *            分隔符
     * @param array
     *            要拼接的数组
     * @return 拼接好的字符串
     */
    public static <T> String join(String sp, T... args) {
        return Lang.concat(sp, args).toString();
    }

    /**
     * 将一个字节数变成人类容易识别的显示字符串，比如 1.5M 等
     * 
     * @param size
     *            字节数
     * @param SZU
     *            千的单位，可能为 1024 或者 1000
     * @return 人类容易阅读的字符串
     */
    private static String _formatSizeForRead(long size, double SZU) {
        if (size < SZU) {
            return String.format("%d bytes", size);
        }
        double n = (double) size / SZU;
        if (n < SZU) {
            return String.format("%5.2f KB", n);
        }
        n = n / SZU;
        if (n < SZU) {
            return String.format("%5.2f MB", n);
        }
        n = n / SZU;
        return String.format("%5.2f GB", n);
    }

    /**
     * @see #_formatSizeForRead(long, double)
     */
    public static String formatSizeForReadBy1024(long size) {
        return _formatSizeForRead(size, 1024);
    }

    /**
     * @see #_formatSizeForRead(long, double)
     */
    public static String formatSizeForReadBy1000(long size) {
        return _formatSizeForRead(size, 1000);
    }

    /**
     * 改变字符编码集
     * 
     * @param cs
     *            原字符串
     * @param newCharset
     *            指定的新编码集
     * @return
     */
    public static String changeCharset(CharSequence cs, Charset newCharset) {
        if (cs != null) {
            byte[] bs = cs.toString().getBytes();
            return new String(bs, newCharset);
        }
        return null;
    }

    /**
     * 将字符串根据转移字符转移
     * 
     * @param str
     *            字符串
     * @return 转移后的字符串
     */
    public static String evalEscape(String str) {
        StringBuilder sb = new StringBuilder();
        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            // 如果是转义字符
            if (c == '\\') {
                c = cs[++i];
                switch (c) {
                case 'n':
                    sb.append('\n');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'b':
                    sb.append('\b');
                    break;
                case '\'':
                case '"':
                case '\\':
                    sb.append(c);
                    break;
                default:
                    throw Lang.makeThrow("evalEscape invalid char[%d] '%c'  : %s", i, c, str);
                }
            }
            // 否则添加
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串按照某个或几个分隔符拆分。 其中，遇到字符串 "..." 或者 '...' 并不拆分
     * 
     * @param str
     *            要被拆分的字符串
     * @param keepQuote
     *            是否保持引号
     * @param seps
     *            分隔符
     * @return 拆分后的数组
     */
    public static String[] split(String str, boolean keepQuote, char... seps) {
        List<String> list = new LinkedList<String>();
        char[] cs = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            // 遇到分隔符号
            if (Nums.isin(seps, c)) {
                if (!Strings.isBlank(sb)) {
                    String s2 = sb.toString();
                    if (!keepQuote)
                        s2 = evalEscape(s2);
                    list.add(s2);
                    sb = new StringBuilder();
                }
            }
            // 如果是转义字符
            else if (c == '\\') {
                i++;
                if (i < cs.length) {
                    c = cs[i];
                    sb.append(c);
                } else {
                    break;
                }
            }
            // 字符串
            else if (c == '\'' || c == '"' || c == '`') {
                if (keepQuote)
                    sb.append(c);
                while (++i < cs.length) {
                    char c2 = cs[i];
                    // 如果是转义字符
                    if (c2 == '\\') {
                        sb.append('\\');
                        i++;
                        if (i < cs.length) {
                            c2 = cs[i];
                            sb.append(c2);
                        } else {
                            break;
                        }
                    }
                    // 退出字符串
                    else if (c2 == c) {
                        if (keepQuote)
                            sb.append(c2);
                        break;
                    }
                    // 其他附加
                    else {
                        sb.append(c2);
                    }
                }
            }
            // 其他，计入
            else {
                sb.append(c);
            }
        }

        // 添加最后一个
        if (!Strings.isBlank(sb)) {
            String s2 = sb.toString();
            if (!keepQuote)
                s2 = evalEscape(s2);
            list.add(s2);
        }

        // 返回拆分后的数组
        return list.toArray(new String[list.size()]);
    }
    
    public static String safeToString(Object obj, String dft) {
        try {
            if (obj == null)
                return "null";
            return obj.toString();
        }
        catch (Exception e) {
        }
        if (dft != null)
            return dft;
        return String.format("/*%s(toString FAILED)*/", obj.getClass().getName());
    }
}
