package org.nutz.mvc2.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.mvc2.ActionFilter;
import org.nutz.mvc2.ActionFilterChain;

/**
 * 执行链的具体实现
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ActionFilterChainImpl implements ActionFilterChain {
	
	private List<ActionFilter> filters;
	
	private Map<Object, Object> content = new HashMap<Object, Object>();
	
	public ActionFilterChainImpl(List<ActionFilter> filters) {
		this.filters = filters;
	}

	public Map<Object, Object> getContent() {
		return content;
	}

	public void doChain() throws Throwable {
		if (filters.isEmpty())
			return;
		filters.remove(0).filter(this);
	}

	public Object get(Object key) {
		return content.get(key);
	}
	
	public void put(Object key, Object value) {
		content.put(key, value);
	}

}
