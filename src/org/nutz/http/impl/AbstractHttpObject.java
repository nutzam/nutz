package org.nutz.http.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

public class AbstractHttpObject {
	
	//---------------------------------------------------------------
	protected ServletContext servletContext;
	public ServletContext getServletContext() {
		return servletContext;
	}
	protected SessionManger sessionManger;
	
	
	//----------------------------------------------------------------
	//Attr相关
	protected Map<String, Object> attrs = new HashMap<String, Object>();
	
	public Object getAttribute(String name) {
		return attrs.get(name);
	}
	
	public void setAttribute(String name, Object o) {
		attrs.put(name, o);
	}
	
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(attrs.keySet());
	}
	
	public void removeAttribute(String name) {
		attrs.remove(name);
	}
}
