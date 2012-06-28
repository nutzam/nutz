package org.nutz.lang.stream;

import java.io.IOException;
import java.io.Writer;

public class StringWriter extends Writer {

    private StringBuilder sb;

    public StringWriter(StringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public void close() throws IOException {}

    @Override
    public void flush() throws IOException {}

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < (off + len); i++) {
            sb.append(cbuf[i]);
        }
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }
}
