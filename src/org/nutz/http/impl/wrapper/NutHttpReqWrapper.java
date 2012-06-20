package org.nutz.http.impl.wrapper;

import javax.servlet.http.HttpServletRequest;

import org.nutz.http.impl.NutHttpReq;

/**
 * 将HttpServletRequest封装为NutHttpReq对象
 * @author Administrator
 *
 */
public class NutHttpReqWrapper extends NutHttpReq {

	protected HttpServletRequest servletReq;
	
	public NutHttpReqWrapper(HttpServletRequest servletReq) {
		this.servletReq = servletReq;
	}
	
	
}
