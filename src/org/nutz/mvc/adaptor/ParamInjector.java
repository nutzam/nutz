package org.nutz.mvc.adaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 参数注入接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ParamInjector {

	/**
	 * @param req
	 *            请求对象
	 * @param resp
	 *            响应对象
	 * @param refer
	 *            参考对象
	 * @return 注入值
	 */
	Object get(HttpServletRequest req, HttpServletResponse resp, Object refer);

}
