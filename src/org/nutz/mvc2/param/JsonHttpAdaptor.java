package org.nutz.mvc2.param;

import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc2.param.injector.JsonInjector;

public class JsonHttpAdaptor extends AbstractHttpAdaptor {

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
	protected ParamInjector evalInjector(Class<?> type, String name) {
		return new JsonInjector(type);
	}

}
