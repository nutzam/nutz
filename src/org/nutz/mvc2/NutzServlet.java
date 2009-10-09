package org.nutz.mvc2;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.db.DatabaseLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.JsonLoader;
import org.nutz.lang.Strings;
import org.nutz.mvc2.url.UrlMapImpl;

@SuppressWarnings("serial")
public class NutzServlet extends HttpServlet {

	private UrlMap urls;

	@Override
	public void init(ServletConfig config) throws ServletException {
		// Load Ioc
		// check the params
		Ioc ioc = null;
		try {
			String[] paths = Strings.splitIgnoreBlank(config.getInitParameter("ioc-by-json"));
			if (null != paths) {
				ioc = new NutIoc(new JsonLoader(paths));
			} else {
				String className = config.getInitParameter("ioc-by-db");
				if (null != className) {
					Class<?> providerClass = Class.forName(className);
					DaoProvider provider = (DaoProvider) providerClass.newInstance();
					Dao dao = provider.getDataSource(config);
					ioc = new NutIoc(new DatabaseLoader(dao));
				}
			}
			// Save ioc object to context
			if (null != ioc)
				getServletContext().setAttribute(Ioc.class.getName(), ioc);
			// Load modules
			urls = new UrlMapImpl(ioc);
			String[] names = Strings.splitIgnoreBlank(config.getInitParameter("modules"));
			if (null != names)
				for (String name : names)
					urls.add(Class.forName(name));
			// Save urls object to contect
			getServletContext().setAttribute(UrlMap.class.getName(), urls);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// format path
		String path = req.getServletPath();
		int lio = path.lastIndexOf('.');
		if (lio > 0)
			path = path.substring(0, lio);

		// get Url and invoke it
		MethodInvoker invoker = urls.get(path);
		invoker.invoke(req, resp);
	}

}
