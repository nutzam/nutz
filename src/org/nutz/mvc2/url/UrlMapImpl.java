package org.nutz.mvc2.url;

import org.nutz.ioc.Ioc;
import org.nutz.mvc2.MethodInvoker;
import org.nutz.mvc2.UrlMap;

public class UrlMapImpl implements UrlMap {
	
	private Ioc ioc;

	public UrlMapImpl(Ioc ioc){
		this.ioc = ioc;
	}

	public UrlMap add(Class<?> module) {
		return null;
	}

	public MethodInvoker get(String url) {
		return null;
	}

}
