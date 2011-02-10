package org.nutz.mvc2.impl;

import org.nutz.mvc2.ActionFilterChain;

public class EncodingFilter extends AbstractActionFilter {

	@Override
	public void filter(ActionFilterChain chain) throws Throwable {
		getRequest(chain).setCharacterEncoding(chain.get(ActionFilters.requestEncoding).toString());
		getResponse(chain).setCharacterEncoding(chain.get(ActionFilters.responseEncoding).toString());
		chain.doChain();
	}
}
