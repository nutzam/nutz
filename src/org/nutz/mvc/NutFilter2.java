package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 为了兼容老的NutFilter,把逻辑独立出来, 仅用于过滤Jsp请求之类的老特性
 *
 */
public class NutFilter2 implements Filter {
	
	private String selfName;
	
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		if (selfName == null) {
			selfName = Mvcs.ctx.nutConfigs.keySet().iterator().next();
			if (selfName == null) {
				chain.doFilter(req, resp);
				return;
			}
		}
		boolean needReset = false;
		if (Mvcs.getName() == null) {
			HttpServletRequest req2 = (HttpServletRequest)req;
			HttpServletResponse resp2 = (HttpServletResponse)resp;
			Mvcs.set(selfName, req2, resp2);
			Mvcs.updateRequestAttributes(req2);
			needReset = true;
		}
		try {
			chain.doFilter(req, resp);
		} finally {
			if (needReset)
				Mvcs.resetALL();
		}
	}

	public void init(FilterConfig conf) throws ServletException {}

	public void destroy() {}

}
