package org.nutz.mvc.param;

import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.param.injector.JsonInjector;

public class JsonAdaptor extends AbstractAdaptor {

	public Object[] adapt(HttpServletRequest request, HttpServletResponse response) {
		// Read all as String
		String str;
		try {
			str = Lang.bufferAll(new InputStreamReader(request.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		// Try to make the args
		Object[] args = new Object[injs.length];
		for (int i = 0; i < injs.length; i++) {
			args[i] = injs[i].get(request, response, str);
		}
		return args;
	}

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		return new JsonInjector(type);
	}

}
