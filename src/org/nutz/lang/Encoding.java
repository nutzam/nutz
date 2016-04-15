package org.nutz.lang;

import java.nio.charset.Charset;

public final class Encoding {

    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String GB2312 = "GB2312";
    public static final String ASCII = "US-ASCII";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF16BE = "UTF-16BE";
    public static final String UTF16LE = "UTF-16LE";
    public static final String UTF16 = "UTF-16";

    public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);
    public static final Charset CHARSET_GBK = Charset.forName(GBK);
    public static final Charset CHARSET_GB2312 = Charset.forName(GB2312);
    public static final Charset CHARSET_ASCII = Charset.forName(ASCII);
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
    public static final Charset CHARSET_UTF16 = Charset.forName(UTF16);
    public static final Charset CHARSET_UTF16BE = Charset.forName(UTF16BE);
    public static final Charset CHARSET_UTF16LE = Charset.forName(UTF16LE);

    public static String defaultEncoding() {
        return Charset.defaultCharset().name();
    }

}
