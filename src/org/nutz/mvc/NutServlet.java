package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.init.DefaultLoading;

/**
 * 挂接到 JSP/Servlet 容器的入口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	private static final Log log = Logs.getLog(NutServlet.class);

	private UrlMap urls;

	@Override
	public void init() throws ServletException {
		try {
			if (log.isInfoEnabled())
				log.infof("Nutz.Mvc[%s] is initializing ...", this.getServletName());
			Stopwatch sw = Stopwatch.begin();

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
			Class<? extends Loading> loadingType;
			LoadingBy lb = modules.getAnnotation(LoadingBy.class);
			if (null != lb)
				loadingType = lb.value();
			else
				loadingType = DefaultLoading.class;

			// Here, we load all Nutz.Mvc configuration
			Loading ing = Mirror.me(loadingType).born(this.getServletConfig());
			ing.load(modules);
			// Then, we store the loading result like this
			urls = ing.getUrls();
			this.getServletContext().setAttribute(UrlMap.class.getName(), urls);
			this.getServletContext().setAttribute(Ioc.class.getName(), ing.getIoc());
			this.getServletContext().setAttribute(Localization.class.getName(), ing.getMessageMap());

			// Done, print info
			sw.stop();
			if (log.isInfoEnabled())
				log.infof("Nutz.Mvc[%s] is up in %sms", this.getServletName(), sw.getDuration());

		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void destroy() {
		if (log.isInfoEnabled())
			log.infof("Nutz.Mvc[%s] is deposing ...", this.getServletName());
		Stopwatch sw = Stopwatch.begin();

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

		// Done, print info
		sw.stop();
		if (log.isInfoEnabled())
			log.infof("Nutz.Mvc[%s] is down in %sms", this.getServletName(), sw.getDuration());
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
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
