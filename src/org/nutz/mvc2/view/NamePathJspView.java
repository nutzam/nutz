package org.nutz.mvc2.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc2.View;

public class NamePathJspView implements View {

	private static final String OBJ_ATTR_NAME = "obj";

	public NamePathJspView(String name) {
		this.path = new StringBuilder("/WEB-INF/").append(name.replace('.', '/')).append(".jsp")
				.toString();
	}

	private String path;

	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws Exception {
		if (null != obj)
			request.setAttribute(OBJ_ATTR_NAME, obj);
		RequestDispatcher rd = request.getRequestDispatcher(path);
		if (rd == null)
			throw new Exception("Could not get RequestDispatcher for [" + path
					+ "]: check up the file existed in your application please!");
		rd.forward(request, response);
	}

}
