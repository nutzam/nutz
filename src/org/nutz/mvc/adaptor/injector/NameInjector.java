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
	 * @Param refer 这个参考字段，如果有值，表示是路径参数的值，那么它比 request 里的参数优先
	 */
	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		String value;
		if (null != refer)
			value = refer.toString();
		else
			value = req.getParameter(name);
		return Castors.me().castTo(value, type);
	}

}
