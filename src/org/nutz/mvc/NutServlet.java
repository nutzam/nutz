package org.nutz.mvc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.init.DefaultLoading;

@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	private UrlMap urls;

	@Override
	public void init() throws ServletException {
		try {
			// Nutz.Mvc need a class name as the default Module
			// it will load some Annotation from it.
			String name = Strings.trim(this.getServletConfig().getInitParameter("modules"));
			if (Strings.isEmpty(name)) {
				throw Lang.makeThrow(ServletException.class,
						"You need declare modules parameter in '%s'", this.getClass().getName());
			}
			Class<?> modules = Class.forName(name);

			// servlet support you setup your loading class, it must implement
			// "org.nutz.mvc.Loading"
			// And it must has one constructor, with one param type as
			// ServletConfig
			Class<? extends Loading> loadingType = DefaultLoading.class;
			LoadingBy lb = modules.getAnnotation(LoadingBy.class);
			if (null != lb)
				loadingType = lb.value();

			// Here, we load all Nutz.Mvc configuration
			Loading ing = Mirror.me(loadingType).born(this.getServletConfig());
			ing.load(modules);
			// Then, we store the loading result like this
			urls = ing.getUrls();
			Map<String, Map<String, String>> msgss = ing.getMessageMap();
			this.getServletContext().setAttribute(UrlMap.class.getName(), urls);
			this.getServletContext().setAttribute(Ioc.class.getName(), ing.getIoc());
			this.getServletContext().setAttribute(Localization.class.getName(), msgss);
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void destroy() {
		// Firstly, upload the user customized desctroy
		try {
			urls = null;
			Setup setup = (Setup) this.getServletContext().getAttribute(Setup.class.getName());
			if (null != setup)
				setup.destroy(getServletConfig());
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		} finally {
			super.destroy();
		}
		// If the application has Ioc, depose it
		Ioc ioc = Mvcs.getIoc(this.getServletContext());
		if (null != ioc)
			ioc.depose();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Mvcs.updateRequestAttributes(req);
		String path = Mvcs.getRequestPath(req);
		// get Url and invoke it
		ActionInvoker invoker = urls.get(path);
		if (null == invoker) {
			resp.setStatus(404);
		} else
			invoker.invoke(req, resp);
	}

}
