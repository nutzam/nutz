package org.nutz.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.Ioc;

public class Mvcs {

	public static Ioc getIoc(HttpServletRequest request) {
		return getIoc(request.getSession().getServletContext());
	}

	public static Ioc getIoc(ServletContext context) {
		return (Ioc) context.getAttribute(Ioc.class.getName());
	}

	public static UrlMap getUrls(ServletContext context) {
		return (UrlMap) context.getAttribute(UrlMap.class.getName());
	}

}
