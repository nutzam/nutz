
package org.nutz.lang.util;
 
import java.io.IOException;
import java.io.InputStream;
 
/**
 * 根据一个 byte[] 数组，构建一个 InputStream
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ByteInputStream extends InputStream {
 
    private byte[] bytes;
 
    private int cursor;
 
    private int length;
 
    public ByteInputStream(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }
 
    public ByteInputStream(byte[] bytes, int off, int len) {
        this.bytes = bytes;
        this.cursor = off;
        this.length = off + len;
        if (this.length > bytes.length)
            this.length = bytes.length;
    }
 
    @Override
    public int read() throws IOException {
        if (cursor < length)
            return bytes[cursor++];
        return -1;
    }
 
}