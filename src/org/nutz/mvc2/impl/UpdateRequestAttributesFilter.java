package org.nutz.mvc2.impl;

import org.nutz.mvc.Mvcs;
import org.nutz.mvc2.ActionFilterChain;

public class UpdateRequestAttributesFilter extends AbstractActionFilter {

	@Override
	public void filter(ActionFilterChain chain) throws Throwable {
		Mvcs.updateRequestAttributes(getRequest(chain));
		chain.doChain();
	}
}
