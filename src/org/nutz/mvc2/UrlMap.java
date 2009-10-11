package org.nutz.mvc2;

import java.util.List;

public interface UrlMap {

	public void add(List<ViewMaker> makers, Class<?> module);

	public ActionInvoker get(String path);

}
