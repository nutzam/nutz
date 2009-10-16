package org.nutz.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class HttpStatusView implements View {

	private int sc;

	public HttpStatusView(int sc) {
		this.sc = sc;
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) {
		resp.setStatus(sc);
	}

}
