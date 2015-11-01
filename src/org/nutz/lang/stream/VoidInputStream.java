package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @since 1.b.53
 * @author zozoh(zozohtnt@gmail.com)
 */
public class VoidInputStream extends InputStream {

    @Override
    public int read() throws IOException {
        return -1;
    }

}
