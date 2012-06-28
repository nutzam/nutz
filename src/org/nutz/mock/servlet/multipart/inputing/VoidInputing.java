package org.nutz.mock.servlet.multipart.inputing;

import java.io.IOException;

public class VoidInputing implements Inputing {

    public void close() throws IOException {}

    public void init() throws IOException {}

    public int read() throws IOException {
        return -1;
    }

    public long size() {
        return 0;
    }

}
