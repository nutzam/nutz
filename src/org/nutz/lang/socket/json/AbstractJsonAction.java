package org.nutz.lang.socket.json;

import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.lang.socket.SocketContext;

public abstract class AbstractJsonAction implements JsonAction {

	public void run(SocketContext context) {}

	public abstract void run(Map<String, Object> data, SocketContext context) ;
	
	protected static void sendJson(SocketContext context, Object obj) {
		Json.toJson(Streams.utf8w(context.getOutputStream()), obj);
	}

}
