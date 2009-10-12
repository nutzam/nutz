package org.nutz.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.init.Launching;

@SuppressWarnings("serial")
public class NutzServlet extends HttpServlet {

	private UrlMap urls;
	private Map<String, String> msgs;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			String name = Strings.trim(config.getInitParameter("modules"));
			if (Strings.isEmpty(name)) {
				throw Lang.makeThrow(ServletException.class,
						"You need declare modules parameter in '%'", this.getClass().getName());
			}
			Launching la = new Launching(config);
			la.launch(Class.forName(name));
			msgs = la.getMsgs();
			urls = la.getUrls();
			config.getServletContext().setAttribute(UrlMap.class.getName(), urls);
			config.getServletContext().setAttribute(Ioc.class.getName(), la.getIoc());
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void destroy() {
		urls = null;
		Setup setup = (Setup) this.getServletContext().getAttribute(Setup.class.getName());
		if (null != setup)
			setup.destroy(getServletConfig());
		Ioc ioc = Mvcs.getIoc(this.getServletContext());
		if (null != ioc)
			ioc.depose();
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// Add Localization Message
		if (null != msgs)
			req.setAttribute("msgs", msgs);
		// format path
		// System.out.println(Printer.dumpObject(req));
		String path = req.getPathInfo();
		int lio = path.lastIndexOf('.');
		if (lio > 0)
			path = path.substring(0, lio);

		// get Url and invoke it
		ActionInvoker invoker = urls.get(path);
		if (null == invoker) {
			resp.setStatus(404);
			resp.flushBuffer();
		} else
			invoker.invoke(req, resp);
	}

}
