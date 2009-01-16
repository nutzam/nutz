package com.zzh.mvc.c;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateEntity<T> extends EntityControllor<T> {

	@Override
	public Object execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T obj = getEntityAsJson(request);
		return getService().update(obj);
	}

}
