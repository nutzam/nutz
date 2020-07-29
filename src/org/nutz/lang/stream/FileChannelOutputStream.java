package org.nutz.lang.stream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.nutz.lang.Streams;

public class FileChannelOutputStream extends OutputStream {

    private FileOutputStream ops;

    private FileChannel chan;

    private ByteBuffer buf;

    public FileChannelOutputStream(FileOutputStream ops) {
        this.ops = ops;
        this.chan = ops.getChannel();
    }

    public FileChannelOutputStream(FileChannel chan) {
        this.chan = chan;
    }

    @Override
    public void write(int b) throws IOException {
        if (null == buf) {
            buf = ByteBuffer.allocate(8192);
            // 切换到写模式
            if (buf.isReadOnly()) {
                buf.flip();
            }
        }
        // 如果已经写满了，那么就写入通道
        if (!buf.hasRemaining()) {
            flushBuffer();
        }
        buf.putInt(b);
    }

    private void flushBuffer() throws IOException {
        // 切换到读模式
        if (!buf.isReadOnly()) {
            buf.flip();
        }
        // 写入到通道
        chan.write(buf);
        // 清空一下
        buf.clear();
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (null != buf && buf.position() > 0) {
            this.flushBuffer();
        }
        ByteBuffer bb = ByteBuffer.wrap(b, off, len);
        chan.write(bb);
    }

    @Override
    public void flush() throws IOException {
        if (null != buf) {
            this.flushBuffer();
        }
        chan.force(false);
        Streams.safeFlush(ops);
    }

    @Override
    public void close() throws IOException {
        Streams.safeClose(chan);
        Streams.safeClose(ops);
    }

}
