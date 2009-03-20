package com.zzh.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Action implements Controllor {

	public String[] paramFields;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Mvc.getObjectAsNameValuePair(this, request, paramFields);
		return execute();
	}

	public abstract Object execute() throws Exception;

}
