package org.nutz.mvc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.nutz.mvc.init.InitException;
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
	private NutMvc nutMvc = NutMvc.make();
	/**
	 * Nutz.Mvc 是否成功的被挂接在 JSP/Servlet 容器上。这个标志位可以为子类提供参考
	 */
	private boolean ok;

	protected boolean isOk() {
		return ok;
	}

	@Override
	public void init() throws ServletException {
		nutMvc.init(new ServletNutConfig(getServletConfig()));
		ok = true;
	}

	public void destroy() {
		nutMvc.destroy();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try{
			if(!nutMvc.handle(req, resp)){
				resp.setStatus(404);
			}
		}catch (InitException e) {
			return;
		}
	}
}
