package com.zzh.mvc.upload;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;

public abstract class AfterUpload implements Controllor {

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return execute(Upload.getParams(request), request, response);
	}

	abstract protected Object execute(Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse response);

}
