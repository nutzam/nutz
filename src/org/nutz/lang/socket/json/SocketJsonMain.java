package org.nutz.lang.socket.json;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.nutz.lang.socket.SocketActionTable;
import org.nutz.lang.socket.SocketAtom;
import org.nutz.lang.socket.SocketLock;
import org.nutz.lang.socket.SocketMain;

public class SocketJsonMain extends SocketMain {

	public SocketJsonMain(	List<SocketAtom> atoms,
							SocketLock lock,
							ServerSocket server,
							ExecutorService service,
							SocketActionTable saTable) {
		super(atoms, lock, server, service, saTable);
	}

	@Override
	protected SocketAtom createSocketAtom(	List<SocketAtom> atoms,
											SocketLock lock,
											Socket socket,
											SocketActionTable saTable) {
		return new SocketJsonAtom(atoms, lock, socket, saTable);
	}
}
