package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.annotation.Parameter;

public abstract class Action implements Controllor {

	private Parameter[] paramFields;

	public Action() {
		this.paramFields = Params.getParameterFields(this.getClass());
	}

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Throwable {
		Params.getObjectAsNameValuePair(this, request, paramFields);
		return execute();
	}

	public abstract Object execute() throws Exception;

}
