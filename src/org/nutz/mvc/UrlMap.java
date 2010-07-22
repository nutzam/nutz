package org.nutz.mvc;

import java.util.List;

public interface UrlMap {

	/**
	 * @param makers
	 * @param module
	 * @return <ul>
	 *         <li>true - 类是一个 module
	 *         <li>false - 类不是一个 module
	 *         </ul>
	 */
	public boolean add(List<ViewMaker> makers, Class<?> module);

	/**
	 * @param path
	 *            请求路径
	 * @return 调用时
	 */
	public ActionInvoking get(String path);

}
