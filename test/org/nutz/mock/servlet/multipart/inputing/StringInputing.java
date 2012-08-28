package org.nutz.mock.servlet.multipart.inputing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.nutz.lang.Lang;
import org.nutz.lang.stream.StringInputStream;

public class StringInputing implements Inputing {

    private InputStream ins;

    StringInputing(String str) {
        ins = Lang.ins(str);
    }
    
    StringInputing(String str, Charset charset) {
        ins = new StringInputStream(str, charset);
    }

    public int read() {
        try {
            return ins.read();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public long size() {
        try {
            return ins.available();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public void close() throws IOException {}

    public void init() throws IOException {}

}
