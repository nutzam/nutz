package org.nutz.mvc.init.module;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@InjectName("requestModule")
@At("/request")
@Ok("json")
public class RequestScopeModule {
	
	private HttpServletRequest request;

	@At
	public String check(){
		return request.toString();
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}
