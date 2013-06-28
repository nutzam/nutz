package org.nutz.net;

import java.io.IOException;
import java.net.Socket;

public interface SocketHandler {

    /**
     * @param socket
     *            套接层
     */
    void handle(Socket socket) throws IOException;

    /**
     * 当发生 IO 异常的时候调用
     * 
     * @param e
     *            异常
     * @param socket
     *            套接层
     */
    void whenError(Socket socket, Throwable e);

}
