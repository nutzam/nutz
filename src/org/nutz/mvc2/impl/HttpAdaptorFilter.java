package org.nutz.mvc2.impl;

import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc2.ActionFilterChain;

public class HttpAdaptorFilter extends AbstractActionFilter {

	@Override
	public void filter(ActionFilterChain chain) throws Throwable {
		HttpAdaptor adaptor = (HttpAdaptor) chain.get(ActionFilters.adaptor);
		String[] pathArgs = (String[]) chain.get(ActionFilters.pathArgs);
		Object[] args = adaptor.adapt(getServletContext(chain), getRequest(chain), getResponse(chain), pathArgs);
		chain.put(ActionFilters.methodArgs, args);
		chain.doChain();
	}
}
