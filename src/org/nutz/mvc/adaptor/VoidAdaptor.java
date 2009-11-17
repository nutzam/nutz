package org.nutz.mvc.adaptor;

import org.nutz.mvc.annotation.Param;

/**
 * 除了 ServletRequest, ServletResponse, HttpSession, HttpContext, Ioc，其他类型的参数
 * 将统统被设为 null。 如果你想让你的入口函数完全控制 request， 你可以采用这个适配器。 因为它不会碰 request 的输入流
 * 
 * @author zozoh
 * 
 */
public class VoidAdaptor extends AbstractAdaptor {

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		return null;
	}

}
