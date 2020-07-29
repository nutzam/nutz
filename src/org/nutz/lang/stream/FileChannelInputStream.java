package org.nutz.lang.stream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.nutz.lang.Streams;

public class FileChannelInputStream extends InputStream {

    private FileInputStream ins;

    private FileChannel chan;

    private ByteBuffer buf;

    private int bufLen;

    private int bufPos;

    public FileChannelInputStream(FileInputStream ins) {
        this.ins = ins;
        this.chan = ins.getChannel();
    }

    public FileChannelInputStream(FileChannel chan) {
        this.chan = chan;
    }

    /**
     * <pre>
                             size()
                               V
     [xxxxxxxxxxxxxxxxxxxxxxxxx] chan
                 |
              position()
                 V
          [xxxxxx] buf
            ^    ^
            |    +-- bufLen 
          bufPos
     * </pre>
     */
    @Override
    public long skip(long n) throws IOException {
        long pos = chan.position();
        // 根据缓冲，偏移
        if (null != buf) {
            pos = pos - bufLen + bufPos;
            // 清空缓冲
            buf = null;
            bufLen = 0;
            bufPos = 0;
        }
        // 获得绝对位置
        long pos2 = Math.max(pos + n, 0);
        pos2 = Math.min(pos2, chan.size());
        chan.position(pos2);
        return pos2 - pos;
    }

    @Override
    public int read() throws IOException {
        // 一个个读，那么先读一点出来
        if (null == buf || bufPos >= bufLen) {
            buf = ByteBuffer.allocate(8192);
            bufLen = chan.read(buf);
            bufPos = 0;
        }
        return buf.getInt(bufPos++);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        // 如果之前读过一点，那么先写回去
        if (null != buf && bufPos < bufLen) {
            int max = Math.min(len, bufLen - bufPos);
            buf.get(b, off, len);
            bufPos += max;
            // 读完了剩下的，清空临时缓存
            if (bufPos >= bufLen) {
                buf = null;
                bufPos = 0;
                bufLen = 0;
            }
            return max;
        }
        ByteBuffer bb = ByteBuffer.wrap(b, off, len);
        return chan.read(bb);
    }

    @Override
    public int available() throws IOException {
        int remain = bufLen - bufPos;
        return remain + (int) (chan.size() - chan.position());
    }

    @Override
    public void close() throws IOException {
        Streams.safeClose(chan);
        Streams.safeClose(ins);
    }

}
