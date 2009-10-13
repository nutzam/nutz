package org.nutz.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class VoidView implements View {

	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws Throwable {}

}
