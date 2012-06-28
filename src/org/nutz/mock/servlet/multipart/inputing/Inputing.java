package org.nutz.mock.servlet.multipart.inputing;

import java.io.IOException;

public interface Inputing {

    long size();

    int read() throws IOException;

    void close() throws IOException;

    void init() throws IOException;

}
