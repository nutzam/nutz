package org.nutz.mvc2.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc2.ActionNode;
import org.nutz.mvc2.ActionChain;

/**
 * 抽象的ActionFilter实现,方便用户实现自己的过滤器
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class AbstractActionNode implements ActionNode {

	public void init(NutConfig config) throws Throwable {
	}

	public void depose() throws Throwable {
	}
	
	protected HttpServletRequest getRequest(ActionChain chain) {
		return (HttpServletRequest) chain.get(ActionFilters.request);
	}
	
	protected HttpServletResponse getResponse(ActionChain chain) {
		return (HttpServletResponse) chain.get(ActionFilters.response);
	}
	
	protected ServletContext getServletContext(ActionChain chain) {
		return (ServletContext) chain.get(ActionFilters.servletContent);
	}
}
