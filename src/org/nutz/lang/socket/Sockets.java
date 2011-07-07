package org.nutz.lang.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.born.Borning;
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
			sOut.close();

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
		Sockets.localListenByLine(	port,
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
	public static void localListenByLine(	int port,
											Map<String, SocketAction> actions,
											ExecutorService service) {
		localListen(port, actions, service, SocketMain.class);
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
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void localListen(	int port,
											Map<String, SocketAction> actions,
											ExecutorService service,
											Class<? extends SocketMain> klass) {
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

			// 循环监听的主程序
			final SocketLock lock = new SocketLock();
			ExecutorService execs = Executors.newCachedThreadPool();
			SocketMain main = null;
			Mirror mirror = Mirror.me(klass);
			Borning<SocketMain> borning = null;
			List<SocketAtom> atoms = new LinkedList<SocketAtom>();
			while (!lock.isStop()) {
				if (log.isDebugEnabled())
					log.debug("create new main thread to wait...");
				if(borning == null)
					borning = mirror.getBorning(atoms, lock, server, service, saTable);
				main = borning.born(new Object[]{atoms, lock, server, service, saTable});

				if (log.isDebugEnabled())
					log.debug("Ready for listen");

				execs.execute(main);

				if (log.isDebugEnabled())
					log.debug("wait for accept ...");

				// 如果没有接受套接字，那么自旋判断是不是有一个连接提示要关闭整个监听
				while (!main.isAccepted()) {
					// System.out.print(".");
					// if(++i%80==0)
					// System.out.println();
					if (log.isDebugEnabled())
						log.debug("wait lock ...");

					synchronized (lock) {
						try {
							lock.wait();
						}
						catch (InterruptedException e) {
							throw Lang.wrapThrow(e);
						}
					}

					if (log.isDebugEnabled())
						log.debugf(	"check main accept [%s], lock [%s]",
									main.isAccepted(),
									lock.isStop());

					if (lock.isStop())
						break;
				}

				if (log.isDebugEnabled())
					log.debug("Created a socket");

			}

			// 关闭所有的监听，退出程序
			if (null != main && !main.isAccepted()) {
				if (log.isInfoEnabled())
					log.info("Notify waiting threads...");

				try {
					Socket ss = new Socket("127.0.0.1", port);
					OutputStream sOut = ss.getOutputStream();
					sOut.write("V~~".getBytes());
					sOut.flush();
					sOut.close();
					ss.close();
				}
				catch (Exception e) {}
			}

			if (log.isInfoEnabled())
				log.info("Stop connected threads...");

			while (!execs.isTerminated())
				execs.shutdown();

			if (log.isInfoEnabled())
				log.info("Close all sockets..");

			try {
				for (SocketAtom atom : atoms)
					Sockets.safeClose(atom.getSocket());
			}
			catch (Exception e) {}

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
