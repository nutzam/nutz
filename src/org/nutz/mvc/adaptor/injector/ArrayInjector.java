package org.nutz.mvc.adaptor.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;

public class ArrayInjector extends NameInjector {

	public ArrayInjector(String name, Class<?> type) {
		super(name, type);
	}

	@Override
	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		if (null != refer)
			return Castors.me().castTo(refer, type);

		String[] values = req.getParameterValues(name);
		if (null == values || values.length == 0)
			return null;
		if (values.length == 1)
			return Castors.me().castTo(values[0], type);
		return Castors.me().castTo(values, type);
	}

}
