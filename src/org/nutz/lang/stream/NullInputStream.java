package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;

public class NullInputStream extends InputStream {

    public int read() throws IOException {
        return -1;
    }

}
