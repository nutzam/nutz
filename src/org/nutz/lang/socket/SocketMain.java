package org.nutz.lang.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;

/**
 * 启动 Socket 的主监听历程
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SocketMain implements Atom {

	private static final Log log = Logs.get();

	private SocketLock lock;

	private ServerSocket server;

	private ExecutorService service;

	private SocketActionTable saTable;

	private boolean accepted;

	private List<SocketAtom> atoms;

	public SocketMain(	List<SocketAtom> atoms,
						SocketLock lock,
						ServerSocket server,
						ExecutorService service,
						SocketActionTable saTable) {
		this.atoms = atoms;
		this.lock = lock;
		this.server = server;
		this.service = service;
		this.saTable = saTable;
	}

	public void run() {
		// 等待得到套接层
		Socket socket;
		try {
			socket = server.accept();
		}
		// 网络通信问题，整个监听
		catch (IOException e) {
			lock.setStop(true);
			throw Lang.wrapThrow(e);
		}
		// 设置标志位，让主程序知道自己已经开始工作了
		finally {
			if (log.isDebugEnabled())
				log.debug("Socket is accepted");

			accepted = true;
		}
		// 执行交互操作
		if (log.isDebugEnabled())
			log.debug("run action in a new thread");

		service.execute(createSocketAtom(atoms, lock, socket, saTable));

		if (log.isDebugEnabled())
			log.debugf("done for my job [%s], notify the lock", accepted);

		synchronized (lock) {
			lock.notify();
		}
	}

	public boolean isAccepted() {
		return accepted;
	}

	public boolean isStop() {
		return lock.isStop();
	}

	protected SocketAtom createSocketAtom(List<SocketAtom> atoms, SocketLock lock, Socket socket, SocketActionTable saTable) {
		return new SocketAtom(atoms, lock, socket, saTable);
	}
}
