package org.nutz.lang.stream;

import java.io.IOException;
import java.io.Reader;

@Deprecated
public class StringReader extends Reader {

    private CharSequence cs;
    private int index;

    public StringReader(CharSequence cs) {
        this.cs = cs;
        index = 0;
    }

    @Override
    public void close() throws IOException {}

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (index >= cs.length())
            return -1;
        int count = 0;
        for (int i = off; i < (off + len); i++) {
            if (index >= cs.length())
                return count;
            cbuf[i] = cs.charAt(index++);
            count++;
        }
        return count;
    }

}
