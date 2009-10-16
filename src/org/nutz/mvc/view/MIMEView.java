package org.nutz.mvc.view;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class MIMEView implements View {

	public MIMEView(File file) {
		
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Throwable {}

}
