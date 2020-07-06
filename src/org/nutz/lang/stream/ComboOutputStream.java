package org.nutz.lang.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.nutz.lang.Lang;

/**
 * 组合多个输入流，一起写入
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ComboOutputStream extends OutputStream {

    private OutputStream[] opss;

    public ComboOutputStream(OutputStream... opss) {
        this.opss = opss;
    }

    public ComboOutputStream(Collection<OutputStream> opss) {
        this.opss = opss.toArray(new OutputStream[opss.size()]);
    }

    public void addStream(OutputStream ops) {
        this.opss = Lang.arrayLast(this.opss, ops);
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream ops : opss) {
            ops.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream ops : opss) {
            ops.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream ops : opss) {
            ops.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream ops : opss) {
            ops.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (OutputStream ops : opss) {
            ops.close();
        }
    }

}
