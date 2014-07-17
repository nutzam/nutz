package org.nutz.lang;

import java.nio.charset.Charset;

public final class Encoding {

    public static final String UTF8 = "UTF-8";
    public static final String GBK = "GBK";
    public static final String GB2312 = "GB2312";

    public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);
    public static final Charset CHARSET_GBK = Charset.forName(GBK);
    public static final Charset CHARSET_GB2312 = Charset.forName(GB2312);
    
    public static String defaultEncoding(){
        return Charset.defaultCharset().name();
    }

}
