package org.nutz.mvc;

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

/**
 * 用作入口方法参数,作为传递视图渲染内容的途径
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ViewModel extends NutMap {

	private static final long serialVersionUID = 4736456730036005390L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this);
	}

}
