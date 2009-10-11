package org.nutz.mvc2.param;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;

public class PairHttpAdaptor extends AbstractHttpAdaptor {

	public Object[] adapt(HttpServletRequest request, HttpServletResponse response) {
		Object[] args = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			ParamBean p = params[i];
			if(isNeedSkip(request, response, args, i, p))
				continue;
			String value = request.getParameter(p.getName());
			args[i] = Castors.me().castTo(value, p.getType());
		}
		return args;
	}

}
