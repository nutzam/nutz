package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * since 1.b.53 用 VoidInputStream 来代替吧
 */
@Deprecated 
public class NullInputStream extends InputStream {

    public int read() throws IOException {
        return -1;
    }

}
