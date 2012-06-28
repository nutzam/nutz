package org.nutz.lang.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {

    private StringBuilder sb;
    private ByteArrayOutputStream baos;
    private String charset;

    public StringOutputStream(StringBuilder sb) {
        this(sb, null);
    }

    public StringOutputStream(StringBuilder sb, String charset) {
        this.sb = sb;
        baos = new ByteArrayOutputStream();
        this.charset = charset;
    }

    /**
     * 完成本方法后,确认字符串已经完成写入后,务必调用flash方法!
     */
    @Override
    public void write(int b) throws IOException {
        if (null == baos)
            throw new IOException("Stream is closed");
        baos.write(b);
    }

    /**
     * 使用StringBuilder前,务必调用
     */
    @Override
    public void flush() throws IOException {
        super.flush();
        baos.flush();
        if (baos.size() > 0) {
            if (charset == null)
                sb.append(new String(baos.toByteArray()));
            else
                sb.append(new String(baos.toByteArray(), charset));
            baos.reset();
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        baos = null;
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

}
