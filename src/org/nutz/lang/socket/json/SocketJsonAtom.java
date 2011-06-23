package org.nutz.lang.socket.json;

import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketActionTable;
import org.nutz.lang.socket.SocketAtom;
import org.nutz.lang.socket.SocketContext;
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
			ops = socket.getOutputStream();
		}
		catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			LinkedHashMap<String, Object> map = Json.fromJson(LinkedHashMap.class, 
			                                        Streams.utf8r(socket.getInputStream()));
			SocketAction action = saTable.get(map.get("cmd").toString());
			if (null != action) {
				SocketContext context = new SocketContext(this);
				if(action instanceof JsonAction)
					((JsonAction)action).run(map,context);
				else
					action.run(context);
			} else {
				Writer writer = Streams.utf8w(socket.getOutputStream());
				Map<String, Object> x = new HashMap<String, Object>();
				x.put("ok", false);
				x.put("msg", "Unknown CMD");
				Json.toJson(writer, x);
				writer.close();
			}
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
