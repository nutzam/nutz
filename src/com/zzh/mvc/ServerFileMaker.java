package com.zzh.mvc;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;

import com.zzh.ioc.ObjectMaker;

public class ServerFileMaker extends ObjectMaker {

	private ServletContext context;

	public ServerFileMaker(ServletContext context) {
		this.context = context;
	}

	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("file");
	}

	@Override
	protected File make(Map<String, Object> properties) {
		String path = context.getRealPath(properties.get("file").toString());
		return new File(path);
	}
}
