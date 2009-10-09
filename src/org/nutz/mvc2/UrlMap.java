package org.nutz.mvc2;

public interface UrlMap {

	public UrlMap add(Class<?> module);

	public MethodInvoker get(String url);

}
