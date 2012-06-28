package org.nutz.lang.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;

public class SocketAtom implements Atom {

    private static final Log log = Logs.get();

    protected Socket socket;

    protected OutputStream ops;

    protected BufferedReader br;

    protected String line;

    protected SocketActionTable saTable;

    protected Context context;

    public SocketAtom(Context context, Socket socket, SocketActionTable saTable) {
        this.context = context;
        this.socket = socket;
        this.saTable = saTable;
    }

    public void run() {
        if (this.context.getBoolean("stop")) {
            if (log.isInfoEnabled())
                log.info("stop=true, so, exit ...."); //线程池里面可能还有有尚未启动的任务
                                                      //所以,这里还需要判断一下
            Sockets.safeClose(socket);
            return;
        }
        
        if (log.isDebugEnabled())
            log.debugf("connect with '%s'", socket.getRemoteSocketAddress().toString());

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ops = socket.getOutputStream();
        }
        catch (IOException e1) {
            return;
        }

        // 开始交互
        try {
            doRun();
        }
        catch (SocketException e) {}
        // 要关闭 socket 监听 ...
        catch (CloseSocketException e) {
            if (log.isInfoEnabled())
                log.info("Catch CloseSocketException , set lock stop");
            context.set("stop", true);
        }
        catch (IOException e) {
            log.error("Error!! ", e);
        }
        // 最后保证关闭
        finally {
            if (log.isDebugEnabled())
                log.debug("Close socket");
            Sockets.safeClose(socket);
        }
    }

    protected void doRun() throws IOException {
        // 预先读取一行
        line = br.readLine();

        // 在这个 socket 中逐行读取 ...
        while (null != line) {
            if (log.isDebugEnabled())
                log.debug("  <<socket<<: " + line);

            SocketAction action = saTable.get(Strings.trim(line));
            if (null != action) {
                SocketContext context = new SocketContext(this);
                // action.run 抛出的异常会被原汁原味的抛到外面，
                // 而本函数的异常则在各个语句被处理了 ^_^
                action.run(context);
            }
            // 继续读取
            line = br.readLine();
        }
    }

    public Socket getSocket() {
        return socket;
    }

}
