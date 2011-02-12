package org.nutz.mvc2.impl;

import org.nutz.mvc2.ActionChain;

/**
 * 为req和resp设置编码
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class EncodingNode extends AbstractActionNode {

	@Override
	public void filter(ActionChain chain) throws Throwable {
		getRequest(chain).setCharacterEncoding(chain.get(ActionFilters.requestEncoding).toString());
		getResponse(chain).setCharacterEncoding(chain.get(ActionFilters.responseEncoding).toString());
		chain.doChain();
	}
}
