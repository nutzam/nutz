package org.nutz.mvc.adaptor.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

public class NameInjector implements ParamInjector {

	protected String name;
	protected Class<?> type;

	public NameInjector(String name, Class<?> type) {
		if (null == name)
			throw Lang.makeThrow("Can not accept null as name, type '%s'", type.getName());
		this.name = name;
		this.type = type;
	}

	/**
	 * @param req
	 *            请求对象
	 * @param resp
	 *            响应对象
	 * @param refer
	 *            这个参考字段，如果有值，表示是路径参数的值，那么它比 request 里的参数优先
	 * @return 注入值
	 */
	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		if (null != refer)
			return Castors.me().castTo(refer, type);

		return Castors.me().castTo(req.getParameter(name), type);
	}

}
