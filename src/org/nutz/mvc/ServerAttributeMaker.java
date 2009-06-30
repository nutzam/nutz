package org.nutz.mvc;

import javax.servlet.ServletContext;

import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Val;

public class ServerAttributeMaker implements ValueMaker {

	private ServletContext context;

	public ServerAttributeMaker(ServletContext context) {
		this.context = context;
	}

	@Override
	public String forType() {
		return Val.server;
	}

	@Override
	public Object make(Val val) {
		return context.getAttribute(val.getValue().toString());
	}
}