package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class NutFilter implements Filter {

	public void destroy() {}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		Mvcs.updateRequestAttributes((HttpServletRequest) req);
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig config) throws ServletException {}

}
