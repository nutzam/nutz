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
	
	private String selfName;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		Mvcs.setServletContext(servletConfig.getServletContext());
		selfName = servletConfig.getServletName();
		Mvcs.set(selfName, null, null);
		NutConfig config = new ServletNutConfig(servletConfig);
		Mvcs.setNutConfig(config);
		handler = new ActionHandler(config);
	}

	public void destroy() {
		Mvcs.resetALL();
		Mvcs.set(selfName, null, null);
		if(handler != null)
			handler.depose();
		Mvcs.setServletContext(null);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Mvcs.resetALL();
		try {
			Mvcs.set(selfName, req, resp);
			if (!handler.handle(req, resp))
				resp.setStatus(404);
		} finally {
			Mvcs.resetALL();
		}
	}
}
