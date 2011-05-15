package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.config.ServletNutConfig;

/**
 * 挂接到 JSP/Servlet 容器的入口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	private ActionHandler handler;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		handler = new ActionHandler(new ServletNutConfig(servletConfig));
	}

	public void destroy() {
		if(handler != null)
			handler.depose();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (!handler.handle(req, resp)) {
			resp.setStatus(404);
		}
	}
}
