package org.nutz.http.impl.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.nutz.lang.Lang;
import org.nutz.web.NutHttpResp;

public class NutHttpServletRespose extends HttpServletResponseWrapper {

	protected NutHttpResp resp;
	public NutHttpServletRespose(NutHttpResp resp) {
		super(_mockResp());
		this.resp = resp;
	}
	
	protected static HttpServletResponse _mockResp;
	protected static HttpServletResponse _mockResp() {
		if (_mockResp == null) {
			_mockResp = (HttpServletResponse) Proxy.newProxyInstance(NutHttpServletRequest.class.getClassLoader(), new Class<?>[]{HttpServletResponse.class}, 
					new InvocationHandler() {
						public Object invoke(Object proxy, Method method, Object[] args)
								throws Throwable {
							throw Lang.noImplement();
						}
					});
		}
		return _mockResp;
	}
}
