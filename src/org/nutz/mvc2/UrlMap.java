package org.nutz.mvc2;

public interface UrlMap {

	public void add(Class<?> module);

	public MethodInvoker get(String url);

}
