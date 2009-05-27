package com.zzh.mvc;

import javax.servlet.ServletConfig;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;

public class ServerConfigMaker implements ValueMaker {

	private ServletConfig config;

	public ServerConfigMaker(ServletConfig config) {
		this.config = config;
	}

	@Override
	public String forType() {
		return Val.config;
	}

	@Override
	public Object make(Val val) {
		String name = val.getValue().toString();
		if ("@name".equals(name))
			return config.getServletName();
		return config.getInitParameter(name);
	}
}
