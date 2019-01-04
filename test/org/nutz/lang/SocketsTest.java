package org.nutz.lang;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketContext;
import org.nutz.lang.socket.Sockets;

public class SocketsTest {
    
    @Test
    public void test_listen_and_send() {
        final int port = 9081;
        final Map<String, SocketAction> actions = new HashMap<String, SocketAction>();
        actions.put("ABC", new SocketAction() {
            public void run(SocketContext context) {
                context.writeLine("DEF");
                context.closeConn();
            }
        });
        actions.put("close", Sockets.doClose());
        new Thread() {
            public void run() {
                Sockets.localListenByLine(port, actions);
            };
        }.start();
        Lang.quiteSleep(1000);
        System.out.println(Sockets.sendText("127.0.0.1", port, "ABC\r\n"));
        System.out.println(Sockets.sendText("127.0.0.1", port, "close\r\n"));
    }
}
