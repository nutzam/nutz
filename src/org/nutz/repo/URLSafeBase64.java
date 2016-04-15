package org.nutz.repo;

import org.nutz.lang.Strings;

public class URLSafeBase64 {

    /**
     * 返回一个不带折行的 Base64 编码的 URL 字符串
     * <p>
     * Note: 根据 RFC 4648 的规定，把『+』变成『-』，把『/』变成『_』，去除末尾填充的『=』
     *
     * @param url 需要编码的 URL
     *
     * @return 不带折行的 Base64 编码的 URL 字符串
     */
    public static String encode(String url) {
        return encode(url.getBytes(), false);
    }

    /**
     * 返回一个 Base64 编码的 URL 字符串
     * <p>
     * Note: 根据 RFC 4648 的规定，把『+』变成『-』，把『/』变成『_』，去除末尾填充的『=』
     *
     * @param url 需要编码的 URL
     * @param lineSep 是否在76个字符处添加折行
     *
     * @return Base64 编码的 URL 字符串
     */
    public static String encode(byte[] url, boolean lineSep) {
        return Base64.encodeToString(url, lineSep).replace('+', '-').replace('/', '_').replaceAll("=", "");
    }

    /**
     * 返回通过 URL Safe Base64 解码后的 URL 字符串。<br>
     * 如果传入的字符串不合法，将返回空字符串。
     *
     * @param url URL Safe Base64 编码的 URL
     *
     * @return URL 字符串
     */
    public static String decode(String base64) {
        StringBuilder sb = new StringBuilder(base64);
        sb.append(Strings.dup('=', 5 - base64.length() % 4 - 1));
        byte[] decode = Base64.decode(sb.toString().replace('-', '+').replace('_', '/'));
        if (decode.length != 0 || decode == null) {
            return new String(decode);
        } else {
            return "";
        }
    }
}
