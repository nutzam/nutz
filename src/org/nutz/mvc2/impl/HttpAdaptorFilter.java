package org.nutz.mvc2.impl;

import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc2.ActionChain;

/**
 * 执行HttpAdaptor,产生方法执行需要的参数
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class HttpAdaptorFilter extends AbstractActionNode {

	@Override
	public void filter(ActionChain chain) throws Throwable {
		HttpAdaptor adaptor = (HttpAdaptor) chain.get(ActionFilters.adaptor);
		String[] pathArgs = (String[]) chain.get(ActionFilters.pathArgs);
		Object[] args = adaptor.adapt(getServletContext(chain), getRequest(chain), getResponse(chain), pathArgs);
		chain.put(ActionFilters.methodArgs, args);
		chain.doChain();
	}
}
