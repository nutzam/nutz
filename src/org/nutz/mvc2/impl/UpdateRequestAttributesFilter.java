package org.nutz.mvc2.impl;

import org.nutz.mvc.Mvcs;
import org.nutz.mvc2.ActionFilterChain;

/**
 * 为req更新message和base属性值
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class UpdateRequestAttributesFilter extends AbstractActionFilter {

	@Override
	public void filter(ActionFilterChain chain) throws Throwable {
		Mvcs.updateRequestAttributes(getRequest(chain));
		chain.doChain();
	}
}
