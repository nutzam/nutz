package org.nutz.http.impl.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.nutz.lang.Lang;
import org.nutz.web.NutHttpReq;

public class NutHttpServletRequest extends HttpServletRequestWrapper {
	protected NutHttpReq req;
	public NutHttpServletRequest(NutHttpReq req) {
		super(_mockReq());
		this.req = req;
	}

	protected static HttpServletRequest _mockReq;
	static HttpServletRequest _mockReq() {
		if (_mockReq == null) {
			_mockReq = (HttpServletRequest) Proxy.newProxyInstance(NutHttpServletRequest.class.getClassLoader(), new Class<?>[]{HttpServletRequest.class}, 
					new InvocationHandler() {
						public Object invoke(Object proxy, Method method, Object[] args)
								throws Throwable {
							throw Lang.noImplement();
						}
					});
		}
		return _mockReq;
	}
}
