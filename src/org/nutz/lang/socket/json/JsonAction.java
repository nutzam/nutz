package org.nutz.lang.socket.json;

import java.util.Map;

import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketContext;

public interface JsonAction extends SocketAction {

	void run(Map<String, Object> data, SocketContext context);
}
