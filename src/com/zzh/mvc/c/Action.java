package com.zzh.mvc.c;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.MvcUtils;


public abstract class Action<S> extends AbstractControllor<S> {

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MvcUtils.getObjectAsNameValuePair(this, request);
		return execute();
	}

	public abstract Object execute() throws Exception;

}
