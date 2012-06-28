package org.nutz.lang;

import java.nio.charset.Charset;

public final class Encoding {

    public static final String UTF8 = "UTF-8";
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    
    public static String defaultEncoding(){
        return Charset.defaultCharset().name();
    }

}
