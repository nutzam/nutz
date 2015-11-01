package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Deprecated since 1.b.53 用 VoidInputStream 来代替吧
 */
public class NullInputStream extends InputStream {

    public int read() throws IOException {
        return -1;
    }

}
