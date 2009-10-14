package org.nutz.mvc.upload.injector;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.mvc.param.injector.NameInjector;

public class MapInjector extends NameInjector {

	public MapInjector(String name, Class<?> type) {
		super(name, type);
	}

	@Override
	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		if (null != refer)
			if (refer instanceof Map<?, ?>) {
				Object value = ((Map<?, ?>) refer).get(name);
				return Castors.me().castTo(value, type);
			}
		return null;
	}

}
