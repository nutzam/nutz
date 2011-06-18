package org.nutz.lang.socket.json;

import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketActionTable;
import org.nutz.lang.socket.SocketAtom;
import org.nutz.lang.socket.SocketLock;
import org.nutz.lang.socket.Sockets;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SocketJsonAtom extends SocketAtom {
	
	private static final Log log = Logs.get();

	public SocketJsonAtom(List<SocketAtom> atoms, SocketLock lock, Socket socket, SocketActionTable saTable) {
		super(atoms, lock, socket, saTable);
	}

	@SuppressWarnings("unchecked")
	public void run() {
		if (log.isDebugEnabled())
			log.debugf("connect with '%s'", socket.getRemoteSocketAddress().toString());

		atoms.add(this);

		try {
			LinkedHashMap<String, Object> map = Json.fromJson(LinkedHashMap.class, 
			                                        Streams.utf8r(socket.getInputStream()));
			Writer writer = Streams.utf8w(socket.getOutputStream());
			SocketAction action = saTable.get(map.get("cmd").toString());
			if (null != action && action instanceof JsonAction) {
				Object re = ((JsonAction)action).run((Map<String, Object>) map.get("data"));
				Json.toJson(writer, re);
			} else {
				Json.toJson(writer, "Unknow CMD");
			}
			writer.close();
		}
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
}
