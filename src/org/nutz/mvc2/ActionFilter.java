package org.nutz.mvc2;

import org.nutz.mvc.init.NutConfig;

/**
 * Action请求过滤器,负责处理请求的全过程
 * @author wendal
 *
 */
public interface ActionFilter {

	void init(NutConfig config) throws Throwable;
	
	void depose() throws Throwable;
	
	void filter(ActionFilterChain chain) throws Throwable;
}
