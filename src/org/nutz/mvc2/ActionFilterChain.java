package org.nutz.mvc2;

import java.util.Map;

public interface ActionFilterChain {

	Map<Object, Object> getContent();
	
	void doChain() throws Throwable;
	
	void put(Object key, Object value);
	
	Object get(Object key);
}
