package org.nutz.mvc.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class JspView implements View {

	public JspView() {
	}

	public JspView(String name) {
		this.name = name;
	}

	private String name;
	private String path;
	private String objectAttName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		if (null == path) {
			this.path = new StringBuilder("/WEB-INF/").append(name.replace('.', '/')).append(".jsp").toString();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getObjectAttName() {
		if (null == this.objectAttName) {
			this.objectAttName = "obj";
		}
		return objectAttName;
	}

	public void setObjectAttName(String objectAttName) {
		this.objectAttName = objectAttName;
	}

	public void render(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		request.setAttribute(getObjectAttName(), obj);
		String path = getPath();
		RequestDispatcher rd = request.getRequestDispatcher(path);
		if (rd == null)
			throw new Exception("Could not get RequestDispatcher for [" + path
					+ "]: check up the file existed in your application please!");
		rd.forward(request, response);
	}

}
