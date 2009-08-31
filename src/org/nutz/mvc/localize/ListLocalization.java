package org.nutz.mvc.localize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Controllor;

public class ListLocalization implements Controllor {

	private String[] list;

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return list;
	}

}
