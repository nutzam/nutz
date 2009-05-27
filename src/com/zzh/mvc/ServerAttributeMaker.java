package com.zzh.mvc;

import javax.servlet.ServletContext;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;

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