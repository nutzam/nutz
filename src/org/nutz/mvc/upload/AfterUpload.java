package org.nutz.mvc.upload;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Controllor;

public abstract class AfterUpload implements Controllor {

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return execute(Upload.getParams(request), request, response);
	}

	abstract protected Object execute(Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse response);

}
