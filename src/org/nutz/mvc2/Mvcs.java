package org.nutz.mvc2;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.Ioc;

public class Mvcs {

	public static Ioc getIoc(HttpServletRequest request) {
		return (Ioc) request.getSession().getServletContext().getAttribute(Ioc.class.getName());
	}

}
