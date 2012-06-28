package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.nutz.lang.Lang;

public class StringInputStream extends InputStream {

    private int cursor;
    private byte [] data;
    
    public StringInputStream(CharSequence s, String charset) {
        if (null != s)
            try {
                if (charset == null)
                    data = s.toString().getBytes();
                else
                    data = s.toString().getBytes(charset);
            }
            catch (UnsupportedEncodingException e) {
                throw Lang.wrapThrow(e);
            }
        else
            data = new byte[0];
        cursor = 0;
    }

    public StringInputStream(CharSequence s) {
        this(s,null);
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if (cursor < data.length)
            return data[cursor++];
        return -1;
    }

    @Override
    public int available() throws IOException {
        return data.length - cursor;
    }

    @Override
    public long skip(long n) throws IOException {
        long len = 0;
        if (n > 0 && cursor < data.length){
            len = (cursor + n);
            if (len > data.length){
                len = data.length - cursor;
                cursor = data.length;
            }
        }else
            len = 0;
        return len;
    }

}
