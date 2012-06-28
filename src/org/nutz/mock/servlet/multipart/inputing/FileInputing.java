package org.nutz.mock.servlet.multipart.inputing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.Streams;

public class FileInputing implements Inputing {

    private InputStream ins;
    private File file;
    private long length;

    public FileInputing(File f) throws FileNotFoundException {
        length = f.length();
        file = f;
    }

    public int read() throws IOException {
        return ins.read();
    }

    public long size() {
        return length;
    }

    public void close() throws IOException {
        ins.close();
    }

    public void init() throws IOException {
        ins = new BufferedInputStream(Streams.fileIn(file));
    }

}
