package org.nutz.mvc.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

public class NamePathJspView implements View {

	private static final String OBJ_ATTR_NAME = "obj";

	public NamePathJspView(String name) {
		if (!Strings.isBlank(name))
			this.jspPath = new StringBuilder("/WEB-INF/").append(name.replace('.', '/')).append(
					".jsp").toString();
	}

	private String jspPath;

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {
		if (null != obj)
			req.setAttribute(OBJ_ATTR_NAME, obj);
		RequestDispatcher rd;
		String path;
		if (Strings.isBlank(jspPath))
			path = "/WEB-INF" + Mvcs.getRequestPath(req) + ".jsp";
		else
			path = jspPath;
		rd = req.getRequestDispatcher(path);
		if (rd == null)
			throw new Exception("Could not get RequestDispatcher for [" + jspPath
					+ "]: check up the file existed in your application please!");
		rd.forward(req, resp);
	}
}
