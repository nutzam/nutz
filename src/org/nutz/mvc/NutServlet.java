package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.init.Inits;
import org.nutz.mvc.init.NutConfig;
import org.nutz.mvc.init.config.ServletNutConfig;
import org.nutz.resource.Scans;

/**
 * 挂接到 JSP/Servlet 容器的入口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	private static final Log log = Logs.getLog(NutServlet.class);

	/**
	 * Nutz.Mvc 的参数映射表
	 */
	private UrlMap urls;

	/**
	 * Nutz.Mvc 是否成功的被挂接在 JSP/Servlet 容器上。这个标志位可以为子类提供参考
	 */
	private boolean ok;

	protected boolean isOk() {
		return ok;
	}

	@Override
	public void init() throws ServletException {
		Scans.me().init(getServletContext());
		Loading ing = Inits.init(new ServletNutConfig(getServletConfig()), false);
		urls = ing.getUrls();
		ok = true;
	}

	public void destroy() {
		NutConfig config = new ServletNutConfig(getServletConfig());
		if (config.getMainModule() != null)
			Inits.destroy(config);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (null == urls) {
			if (log.isErrorEnabled())
				log.error("!!!This servlet is destroyed!!! Noting to do!!!");
			return;
		}

		Mvcs.updateRequestAttributes(req);
		String path = Mvcs.getRequestPath(req);

		if (log.isInfoEnabled())
			log.info(path);

		// get Url and invoke it
		ActionInvoking ing = urls.get(path);
		if (null == ing || null == ing.getInvoker())
			resp.setStatus(404);
		else
			ing.invoke(req, resp);
	}
}
