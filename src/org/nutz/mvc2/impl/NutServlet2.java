package org.nutz.mvc2.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.NutServlet;
import org.nutz.mvc2.ActionChainFactory;

@SuppressWarnings("serial")
public class NutServlet2 extends NutServlet {
	
	private ActionChainFactory chainFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		String filterFactoryClass = config.getInitParameter("filterFactoryClass");
		if (filterFactoryClass != null) {
			try {
				chainFactory = (ActionChainFactory) Class.forName(filterFactoryClass).newInstance();
			} catch (Throwable e) {
				throw new ServletException(e);
			}
		} else
			chainFactory = new DefaultActionChainFactory();
		chainFactory.init(config);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		chainFactory.destroy();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			chainFactory.make(req, resp, getServletContext()).doChain();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
	
}
