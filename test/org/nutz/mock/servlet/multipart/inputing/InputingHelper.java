package org.nutz.mock.servlet.multipart.inputing;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

import org.nutz.lang.Lang;

public class InputingHelper {

    private String charset;

    public InputingHelper(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    public Inputing wrap(String fmt, Object... args) {
        return new StringInputing(String.format(fmt, args) + "\r\n", Charset.forName(charset));
    }

    public Inputing name(String name) {
        return wrap("Content-Disposition: form-data; name=\"%s\"", name);
    }

    public Inputing fileName(String name, String fileName) {
        return wrap("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", name, fileName);
    }

    public Inputing contentType(String contentType) {
        return wrap("Content-Type: %s", contentType);
    }

    public Inputing blankLine() {
        return new StringInputing("\r\n");
    }

    public Inputing boundary(String boundary) {
        return new StringInputing("--" + boundary);
    }

    public Inputing data(String str) {
        return new StringInputing(str, Charset.forName(charset));
    }

    public Inputing file(File f) {
        try {
            return new FileInputing(f);
        }
        catch (FileNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }
}
