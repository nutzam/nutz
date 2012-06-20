package org.nutz.http.impl.wrapper;

import javax.servlet.http.HttpServletResponse;

import org.nutz.http.impl.NutHttpResp;

public class NutHttpRespWrapper extends NutHttpResp {

	protected HttpServletResponse servletResp;
	
	public NutHttpRespWrapper(HttpServletResponse servletResp) {
		this.servletResp = servletResp;
	}
}
