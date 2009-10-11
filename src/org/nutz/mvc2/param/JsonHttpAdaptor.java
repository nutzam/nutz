package org.nutz.mvc2.param;

import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

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
		Object[] args = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			ParamBean p = params[i];
			if(isNeedSkip(request, response, args, i, p))
				continue;
			args[i] = Json.fromJson(p.getType(), str);
		}
		return args;
	}

}
