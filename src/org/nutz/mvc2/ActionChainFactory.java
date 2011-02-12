package org.nutz.mvc2;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.init.NutConfig;

/**
 * Action过滤器的提供工厂
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface ActionChainFactory {

	ActionChain make(HttpServletRequest req, HttpServletResponse resp, 
			ServletContext servletContext) throws Throwable;
	
	void init(NutConfig config );
	
	void destroy();
}
