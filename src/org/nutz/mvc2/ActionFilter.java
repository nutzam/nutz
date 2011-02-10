package org.nutz.mvc2;

import org.nutz.mvc.init.NutConfig;

/**
 * Action请求过滤器
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface ActionFilter {

	void init(NutConfig config) throws Throwable;
	
	void depose() throws Throwable;
	
	void filter(ActionFilterChain chain) throws Throwable;
}
