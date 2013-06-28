package org.nutz.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 提供一个简易的 TCP 的 socket 监听服务器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TcpServer implements Runnable {

    private static final Log log = Logs.get();

    private boolean stop;

    private int port;

    private ServerSocket listener;

    private SocketHandler handler;

    public TcpServer(int port, SocketHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    protected void listen(Socket socket) {
        SocketHandler handler = getHandler();
        // 处理请求
        try {
            handler.handle(socket);
        }
        // 仅仅是关闭连接
        catch (SocketClosed e) {}
        // 停止服务
        catch (ServerStopped e) {
            stop = true;
        }
        // 处理异常
        catch (Throwable e) {
            handler.whenError(socket, e);
        }
        // 确保关闭
        finally {
            if (!socket.isClosed())
                try {
                    socket.close();
                }
                catch (IOException e) {
                    throw Lang.wrapThrow(e);
                }
        }
    }

    public void run() {
        // ----------------------------------------- 建立
        log.infof("start TcpServer [%s] @ %d", Thread.currentThread().getName(), port);
        try {
            listener = new ServerSocket(port);
        }
        catch (IOException e1) {
            throw Lang.wrapThrow(e1);
        }
        // ----------------------------------------- 循环
        log.infof("TcpServer listen @ %d", port);
        while (!stop) {
            log.info("before accept ...");
            Socket socket = null;
            try {
                socket = listener.accept();
            }
            catch (IOException e) {
                log.fatalf("Fail to accept %s @ %d , System.exit!", Thread.currentThread()
                                                                          .getName(), port);
                System.exit(0);
            }

            log.info("do listen ...");
            listen(socket);
            log.infof("done for listen [%s:%d], stop=%b",
                      socket.getInetAddress().getHostName(),
                      socket.getPort(),
                      stop);

        }
        // ----------------------------------------- 关闭
        try {
            listener.close();
            log.infof("TcpServer shutdown @ %d", port);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public SocketHandler getHandler() {
        return handler;
    }

    public void setHandler(SocketHandler handler) {
        this.handler = handler;
    }

    public void stop() {
        stop = true;
    }

}
