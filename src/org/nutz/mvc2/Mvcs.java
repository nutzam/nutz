package org.nutz.mvc2;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.Ioc;

public class Mvcs {

	public static Ioc getIoc(HttpServletRequest request) {
		return (Ioc) request.getSession().getServletContext().getAttribute(Ioc.class.getName());
	}

	public static UrlMap getUrls(ServletContext context) {
		return (UrlMap) context.getAttribute(UrlMap.class.getName());
	}

}
