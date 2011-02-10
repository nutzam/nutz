package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc.init.config.ServletNutConfig;

/**
 * 挂接到 JSP/Servlet 容器的入口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {
	
	private NutMvcContent mvcContent = new NutMvcContent();
	
	protected NutConfig config;
	
	/**
	 * Nutz.Mvc 是否成功的被挂接在 JSP/Servlet 容器上。这个标志位可以为子类提供参考
	 */
	private boolean ok;

	protected boolean isOk() {
		return ok;
	}

	@Override
	public void init() throws ServletException {
		config = new ServletNutConfig(getServletConfig());
		mvcContent.init(config);
		ok = true;
	}

	public void destroy() {
		mvcContent.destroy();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(!mvcContent.handle(req, resp)){
			resp.setStatus(404);
		}
	}
}
