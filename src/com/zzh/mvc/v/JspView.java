package com.zzh.mvc.v;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspView extends AbstractView {

	public JspView() {
		super();
	}

	public JspView(String name) {
		super(name);
	}

	private ServletContext servletContext;

	public void setServletContext(ServletContext sc) {
		this.servletContext = sc;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	private String path;

	private String objectAttName;

	public String getPath() {
		if (null == path) {
			this.path = new StringBuilder("/WEB-INF/").append(name.replace('.', '/'))
					.append(".jsp").toString();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getObjectName() {
		if (null == this.objectAttName) {
			this.objectAttName = "obj";
		}
		return objectAttName;
	}

	public void setObjectName(String objectName) {
		this.objectAttName = objectName;
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object value)
			throws Exception {
		request.setAttribute(getObjectName(), value);
		String path = getPath();
		RequestDispatcher rd = request.getRequestDispatcher(path);
		if (rd == null)
			throw new ServletException("Could not get RequestDispatcher for [" + path
					+ "]: check up the file existed in your application please!");
		rd.forward(request, response);
	}

}
