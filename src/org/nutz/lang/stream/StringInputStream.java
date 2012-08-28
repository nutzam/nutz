package org.nutz.lang.stream;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.nutz.lang.Encoding;

public class StringInputStream extends ByteArrayInputStream {

    public StringInputStream(CharSequence s, Charset charset) {
        super(toBytes(s, charset));
    }

    public StringInputStream(CharSequence s) {
        super(toBytes(s, Encoding.CHARSET_UTF8));
    }
    
    protected static byte[] toBytes(CharSequence str, Charset charset) {
        if (str == null)
            return new byte[0];
        if (charset == null)
            charset = Encoding.CHARSET_UTF8;
        return str.toString().getBytes(charset);
    }
}
