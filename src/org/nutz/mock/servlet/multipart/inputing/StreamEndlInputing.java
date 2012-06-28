package org.nutz.mock.servlet.multipart.inputing;

import java.io.IOException;

public class StreamEndlInputing implements Inputing {

    private static final int[] ary = {'-', '-', '\r', '\n'};

    private int index;

    public StreamEndlInputing() {
        index = 0;
    }

    public int read() {
        if (index < ary.length)
            return ary[index++];
        return -1;
    }

    public long size() {
        return 4;
    }

    public void close() throws IOException {}

    public void init() throws IOException {}

}
