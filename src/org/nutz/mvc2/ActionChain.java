package org.nutz.mvc2;

import java.util.Map;

/**
 * Action过滤器执行链
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface ActionChain {

	Map<Object, Object> getContent();
	
	void doChain() throws Throwable;
	
	void put(Object key, Object value);
	
	Object get(Object key);
}
