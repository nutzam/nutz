package com.zzh.mvc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.zzh.lang.Strings;

public class Server {

	private ServletContext context;
	private ServletConfig config;

	public Server(ServletConfig config) {
		this.context = config.getServletContext();
		this.config = config;
	}

	public String getContextPath() {
		return context.getContextPath();
	}

	public String getUrlExtention() {
		String ext = config.getInitParameter("extention");
		return Strings.isBlank(ext) ? "nut" : ext;
	}
}
