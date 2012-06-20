package org.nutz.http.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpObject {
	
	protected SessionManger sessionManger;
	
	//----------------------------------------------------------------
	//Attr相关
	protected Map<String, Object> attrs = new HashMap<String, Object>();
	public Map<String, Object> attrs() {
		return attrs;
	}
	
	public Object getAttribute(String name) {
		return attrs.get(name);
	}
	
	public void setAttribute(String name, Object o) {
		attrs.put(name, o);
	}
	
	public Set<String> getAttributeNames() {
		return attrs.keySet();
	}
	
	public void removeAttribute(String name) {
		attrs.remove(name);
	}
}
