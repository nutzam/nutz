package org.nutz.mvc2.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.NutServlet;
import org.nutz.mvc2.ActionFilter;
import org.nutz.mvc2.ActionFilterChain;
import org.nutz.mvc2.ActionFilterFactory;

@SuppressWarnings("serial")
public class NutServlet2 extends NutServlet {
	
	private List<ActionFilter> filters;
	
	@Override
	public void init() throws ServletException {
		super.init();
		String filterFactoryClass = config.getInitParameter("filterFactoryClass");
		if (filterFactoryClass != null) {
			try {
				((ActionFilterFactory) Class.forName(filterFactoryClass).newInstance()).get(config);
			} catch (Throwable e) {
				throw new ServletException(e);
			}
		} else
			filters = ActionFilters.defaultFilters();
		for (ActionFilter filter : filters) {
			try {
				filter.init(config);
			} catch (Throwable e) {
				throw new ServletException(e);
			}
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		for (ActionFilter filter : filters) {
			try {
				filter.depose();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ActionFilterChain chain = new ActionFilterChainImpl(new ArrayList<ActionFilter>(filters));
		chain.put(ActionFilters.request, req);
		chain.put(ActionFilters.response, resp);
		chain.put(ActionFilters.servletContent, getServletContext());
		try {
			chain.doChain();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
	
}
