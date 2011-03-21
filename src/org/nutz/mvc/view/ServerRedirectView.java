package org.nutz.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 重定向视图
 * <p>
 * 在入口函数上声明：
 * <p>
 * '@Ok("redirect:/pet/list.nut")'
 * <p>
 * 实际上相当于：<br>
 * new ServerRedirectView("/pet/list.nut");
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ServerRedirectView extends AbstractPathView {

	public ServerRedirectView(String dest) {
		super(dest);
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {

		String path = evalPath(req, obj);

		// Another site
		if (path.startsWith("http://") || path.startsWith("https://")) {}
		// Absolute path, add the context path for it
		else if (path.length() > 0 && path.charAt(0) == '/') {
			path = req.getContextPath() + path;
		}
		// Relative path, add current URL path for it
		else {
			String myPath = req.getPathInfo();
			int pos = myPath.lastIndexOf('/');
			if (pos > 0)
				path = myPath.substring(0, pos) + "/" + path;
			else
				path = "/" + path;
		}
		resp.sendRedirect(path);
		resp.flushBuffer();
	}

}
