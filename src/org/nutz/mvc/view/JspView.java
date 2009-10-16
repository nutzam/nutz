package org.nutz.mvc.view;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class JspView implements View {

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		RequestDispatcher rd = req.getRequestDispatcher(path);
		if (null == rd)
			resp.setStatus(404);
		else
			rd.forward(req, resp);
	}

}
