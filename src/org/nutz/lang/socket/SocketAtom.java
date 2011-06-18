package org.nutz.lang.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import org.nutz.lang.Strings;
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

	protected SocketLock lock;

	protected List<SocketAtom> atoms;

	protected SocketAtom(List<SocketAtom> atoms, SocketLock lock, Socket socket, SocketActionTable saTable) {
		this.atoms = atoms;
		this.lock = lock;
		this.socket = socket;
		this.saTable = saTable;
	}

	public void run() {
		if (log.isDebugEnabled())
			log.debugf("connect with '%s'", socket.getRemoteSocketAddress().toString());

		atoms.add(this);

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
		catch (IOException e) {
			log.error("Error!! ", e);
		}
		finally {
			Sockets.safeClose(socket);
			// 移除自己
			atoms.remove(this);

			if (log.isDebugEnabled())
				log.debug("Done and notify lock");

			synchronized (lock) {
				lock.notify();
			}
		}
	}

	private void doRun() throws IOException {
		// 预先读取一行
		line = br.readLine();

		// 在这个 socket 中逐行读取 ...
		while (null != line) {
			if (log.isDebugEnabled())
				log.debug("  <<socket<<: " + line);

			SocketAction action = saTable.get(Strings.trim(line));
			if (null != action) {
				SocketContext context = new SocketContext(this);
				try {
					// action.run 抛出的异常会被原汁原味的抛到外面，而 本函数的异常则
					// 在各个语句被处理了 ^_^
					action.run(context);
				}
				// 要关闭 socket 监听 ...
				catch (CloseSocketException e) {
					lock.setStop(true);
					break;
				}
			}
			// 继续读取
			line = br.readLine();
		}
	}

	public Socket getSocket() {
		return socket;
	}

}
