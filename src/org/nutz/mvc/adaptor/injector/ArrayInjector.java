package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Array;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

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

		if (values.length == 1) {
			// 如果只有一个值，那么试图直接转换
			try {
				return Castors.me().castTo(values[0], type);
			}
			// zzh: 如果不成，按数组转换
			catch (Exception e) {
				Object re = Array.newInstance(type.getComponentType(), 1);
				Object v = Castors.me().castTo(values[0], type.getComponentType());
				Array.set(re, 0, v);
				return re;
			}
		}
		return Lang.array2array(values, type.getComponentType());
	}

}
