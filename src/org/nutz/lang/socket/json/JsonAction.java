package org.nutz.lang.socket.json;

import java.util.Map;

public interface JsonAction {

	Object run(Map<String, Object> data);
}
