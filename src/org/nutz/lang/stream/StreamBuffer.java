package org.nutz.lang.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class StreamBuffer extends InputStream {

    private static class OutputStreamBuffer extends OutputStream {
        private ArrayList<byte[]> bytes = new ArrayList<byte[]>();
        private int width = 1024;
        private int index = 0;
        private int cursor = 0;

        @Override
        public void write(int b) throws IOException {
            if (cursor >= width)
                index++;
            byte[] row = bytes.size() > index ? bytes.get(index) : null;
            if (null == row) {
                row = new byte[width];
                bytes.add(row);
                cursor = 0;
            }
            row[cursor++] = (byte)b;
        }

        private int size() {
            return index > 0 ? width * (index - 1) + cursor : cursor;
        }

    }

    private OutputStreamBuffer buffer = new OutputStreamBuffer();
    private int index = 0;
    private int cursor = 0;

    public OutputStream getBuffer() {
        return buffer;
    }

    public void write(int b) throws IOException {
        buffer.write(b);
    }

    @Override
    public int read() throws IOException {
        if (cursor > buffer.width) {
            index++;
            cursor = 0;
        }
        if (index > buffer.index)
            return -1;
        if (index < buffer.bytes.size()) {
            byte[] cs = buffer.bytes.get(index);
            if (cursor < buffer.cursor)
                return cs[cursor++];
        }
        return -1;
    }

    @Override
    public int available() throws IOException {
        return buffer.size();
    }

    @Override
    public synchronized void reset() throws IOException {
        index = 0;
        cursor = 0;
    }

    @Override
    public String toString() {
        try {
            return toString(Encoding.defaultEncoding());
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public String toString(String charset) throws IOException {
        index = 0;
        cursor = 0;
        StringBuilder sb = new StringBuilder();
        StringOutputStream sos = new StringOutputStream(sb, charset);
        byte c;
        while ((c = (byte) this.read()) != -1)
            sos.write(c);
        Streams.safeFlush(sos);
        Streams.safeClose(sos);
        return sb.toString();
    }

}
