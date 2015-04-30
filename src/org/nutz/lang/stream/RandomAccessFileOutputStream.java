package org.nutz.lang.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileOutputStream extends OutputStream {

    private RandomAccessFile raf;

    public RandomAccessFileOutputStream(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public void write(int b) throws IOException {
        raf.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        raf.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        raf.write(b, off, len);
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws IOException {
        raf.close();
    }

}
