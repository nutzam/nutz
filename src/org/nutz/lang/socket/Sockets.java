package org.nutz.lang.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.born.Borning;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class Sockets {

    private static final Log log = Logs.get();

    /**
     * 向某主机发送一些字节内容，并将返回写入输出流
     * 
     * @param host
     *            主机
     * @param port
     *            端口
     * @param ins
     *            发送的内容
     * @param ops
     *            主机返回的输入流
     */
    public static void send(String host, int port, InputStream ins, OutputStream ops) {
        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName(host), port);
            // 发送关闭命令
            OutputStream sOut = socket.getOutputStream();
            Streams.write(sOut, ins);
            sOut.flush();

            // 接收服务器的反馈
            if (!socket.isClosed()) {
                InputStream sReturn = socket.getInputStream();
                Streams.write(ops, sReturn);
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(ins);
            Streams.safeClose(ops);
            safeClose(socket);
        }
    }

    /**
     * 向某主机发送一段文本，并将主机的返回作为文本返回
     * 
     * @param host
     *            主机
     * @param port
     *            端口
     * @param text
     *            发送的内容
     * @return 主机返回的文本
     */
    public static String sendText(String host, int port, String text) {
        StringBuilder sb = new StringBuilder();
        send(host, port, Lang.ins(text), Lang.ops(sb));
        return sb.toString();
    }

    /**
     * 监听本地某一个端口，仅仅收到某一个特殊命令时，才会开始一个动作。
     * <p>
     * 并且原生的，它支持输入 "close|stop|bye|exit" 来结束本地监听
     * 
     * @param port
     *            要监听的端口
     * @param line
     *            命令名称
     * @param action
     *            动作执行类
     */
    public static void localListenOneAndStop(int port, String line, SocketAction action) {
        Map<String, SocketAction> actions = createActions();
        actions.put(line, action);
        actions.put("$:^(close|stop|bye|exit)$", doClose());
        localListenByLine(port, actions);
    }

    /**
     * 监听本地某一个端口，仅仅收到某一个特殊命令时，才会开始一个动作。
     * 
     * @param port
     *            要监听的端口
     * @param line
     *            命令名称
     * @param action
     *            动作执行类
     */
    public static void localListenOne(int port, String line, SocketAction action) {
        Map<String, SocketAction> actions = createActions();
        actions.put(line, action);
        localListenByLine(port, actions);
    }

    /**
     * 对于一个 CPU 默认起的处理线程数
     */
    private static final int DEFAULT_POOL_SIZE = 10;

    /**
     * 简化了一个参数，采用默认线程数
     * 
     * @see org.nutz.lang.socket.Sockets#localListenByLine(int, Map, int)
     */
    public static void localListenByLine(int port, Map<String, SocketAction> actions) {
        Sockets.localListenByLine(port, actions, DEFAULT_POOL_SIZE);
    }

    /**
     * 监听本地某一个端口，根据用户输入的命令的不同，执行不同的操作
     * <p>
     * 当然，你如果想让一个过程处理多种命令，请给的 key 前用 "REGEX:" 作为前缀，后面用一个正则表达式 来表示你的你要的匹配的命令 <br>
     * "REGEX:!" 开头的，表示后面的正则表达式是一个命令过滤，所有没有匹配上的命令都会被处理
     * 
     * @param port
     *            要监听的端口
     * @param actions
     *            动作执行类映射表
     * @param poolSize
     *            针对一个 CPU 你打算启动几个处理线程
     * 
     * @see org.nutz.lang.socket.Sockets#localListenByLine(int, Map,
     *      ExecutorService)
     */
    public static void localListenByLine(int port, Map<String, SocketAction> actions, int poolSize) {
        Sockets.localListenByLine(    port,
                                    actions,
                                    Executors.newFixedThreadPool(Runtime.getRuntime()
                                                                        .availableProcessors()
                                                                    * poolSize));
    }

    /**
     * 监听本地某一个端口，根据用户输入的命令的不同，执行不同的操作
     * <p>
     * 当然，你如果想让一个过程处理多种命令，请给的 key 前用 "REGEX:" 作为前缀，后面用一个正则表达式 来表示你的你要的匹配的命令 <br>
     * "REGEX:!" 开头的，表示后面的正则表达式是一个命令过滤，所有没有匹配上的命令都会被处理
     * 
     * @param port
     *            要监听的端口
     * @param actions
     *            动作执行类映射表
     * @param service
     *            线程池的实现类
     */
    public static void localListenByLine(    int port,
                                            Map<String, SocketAction> actions,
                                            ExecutorService service) {
        localListen(port, actions, service, SocketAtom.class);
    }

    /**
     * 监听本地某一个端口，根据用户输入的命令的不同，执行不同的操作
     * <p>
     * 当然，你如果想让一个过程处理多种命令，请给的 key 前用 "REGEX:" 作为前缀，后面用一个正则表达式 来表示你的你要的匹配的命令 <br>
     * "REGEX:!" 开头的，表示后面的正则表达式是一个命令过滤，所有没有匹配上的命令都会被处理
     * 
     * @param port
     *            要监听的端口
     * @param actions
     *            动作执行类映射表
     * @param service
     *            线程池的实现类
     */
    @SuppressWarnings("rawtypes")
    public static void localListen(    int port,
                                    Map<String, SocketAction> actions,
                                    ExecutorService service,
                                    Class<? extends SocketAtom> klass) {
        try {
            // 建立动作映射表
            SocketActionTable saTable = new SocketActionTable(actions);

            // 初始化 socket 接口
            final ServerSocket server;
            try {
                server = new ServerSocket(port);
            }
            catch (IOException e1) {
                throw Lang.wrapThrow(e1);
            }

            if (log.isInfoEnabled())
                log.infof("Local socket is up at :%d with %d action ready", port, actions.size());

            final Context context = Lang.context();
            context.set("stop", false);
            /*
             * 启动一个守护线程，判断是否该关闭 socket 服务
             */
            (new Thread() {
                @Override
                public void run() {
                    setName("Nutz.Sockets monitor thread");
                    while (true) {
                        try {
                            Thread.sleep(1000);
                            if (context.getBoolean("stop")) {
                                try {
                                    server.close();
                                }
                                catch (Throwable e) {}
                                return;
                            }
                        }
                        catch (Throwable e) {}
                    }
                }
            }).start();
            /*
             * 准备 SocketAtom 的生成器
             */
            Borning borning = Mirror.me(klass).getBorningByArgTypes(Context.class,
                                                                    Socket.class,
                                                                    SocketActionTable.class);
            if (borning == null) {
                log.error("boring == null !!!!");
                return;
            }
            /*
             * 进入监听循环
             */
            while (!context.getBoolean("stop")) {
                try {
                    if (log.isDebugEnabled())
                        log.debug("Waiting for new socket");
                    Socket socket = server.accept();
                    if (context.getBoolean("stop")) {
                        Sockets.safeClose(socket);
                        break;// 监护线程也许还是睡觉,还没来得及关掉哦,所以自己检查一下
                    }
                    if (log.isDebugEnabled())
                        log.debug("accept a new socket, create new SocketAtom to handle it ...");
                    Runnable runnable = (Runnable) borning.born(new Object[]{    context,
                                                                                socket,
                                                                                saTable});
                    service.execute(runnable);
                }
                catch (Throwable e) {
                    log.info("Throwable catched!! maybe ask to exit", e);
                }
            }

            if (!server.isClosed()) {
                try {
                    server.close();
                }
                catch (Throwable e) {}
            }

            log.info("Seem stop signal was got, wait 15 for all running thread");

            try {
                service.shutdown();
                service.awaitTermination(15, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {}
            try {
                service.shutdownNow();
            }
            catch (Throwable e2) {}
        }
        catch (RuntimeException e) {
            throw e;
        }
        finally {
            if (log.isInfoEnabled())
                log.info("Stop services ...");
            service.shutdown();
        }

        if (log.isInfoEnabled())
            log.infof("Local socket is down for :%d", port);

    }

    /**
     * 安全关闭套接层，容忍 null
     * 
     * @param socket
     *            套接层
     * @return 一定会返回 null
     */
    public static Socket safeClose(Socket socket) {
        if (null != socket)
            try {
                socket.close();
                socket = null;
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        return null;
    }

    /**
     * 创建一个停止监听的动作对象
     * 
     * @return 动作对象
     */
    public static SocketAction doClose() {
        return new SocketAction() {
            public void run(SocketContext context) {
                throw new CloseSocketException();
            }
        };
    }

    /**
     * 这个函数可以在你的 SocketAction 实现类里被调用，用来关闭当前的监听星闻
     */
    public static void close() {
        throw new CloseSocketException();
    }

    /**
     * 快捷创建动作映射表的方法
     * 
     * @return 动作映射表
     */
    public static Map<String, SocketAction> createActions() {
        Map<String, SocketAction> actions = new HashMap<String, SocketAction>();
        return actions;
    }

}
