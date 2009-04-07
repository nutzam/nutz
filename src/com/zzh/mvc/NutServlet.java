package com.zzh.mvc;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.ioc.Nut;
import com.zzh.ioc.MappingLoader;
import com.zzh.ioc.json.JsonMappingLoader;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;
import com.zzh.mvc.localize.Localizations;

@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			/*
			 * eval MappingLoader
			 */
			MappingLoader loader = null;
			if (!Strings.isBlank(config.getInitParameter("mvc-by-json"))) {
				loader = new JsonMappingLoader(Strings.splitIgnoreBlank(config
						.getInitParameter("mvc-by-json")));
			} else if (!Strings.isBlank(config.getInitParameter("mvc-by-db"))) {
				throw Lang.makeThrow("This feature will coming soonly :P");
			} else {
				throw Lang.makeThrow("You must setup one of parameters: '%s' | '%s'",
						"mvc-by-json", "mvc-by-db");
			}
			/*
			 * Store nut
			 */
			Nut nut = new Nut(new MvcMappingLoader(loader));
			getServletContext().setAttribute(Nut.class.getName(), nut);
			NutMvc mvc = new NutMvc(nut, config);
			Mvc.setMvcSupport(getServletContext(), mvc);
			/*
			 * Store MvcSupport
			 */
			getServletContext().setAttribute(MvcSupport.class.getName(), mvc);
			/*
			 * init local
			 */
			String msgDirPath = config.getInitParameter("msg-dir");
			File msgDir = Files.findFile(msgDirPath);
			if (!msgDir.exists()) {
				msgDir = new File(config.getServletContext().getRealPath(msgDirPath));
			}
			if (!msgDir.exists())
				throw Lang.makeThrow("Can not access message file directory '%s'", msgDirPath);
			String msgSuffix = config.getInitParameter("msg-suffix");
			Localizations.init(config.getServletContext(), msgDir, msgSuffix);
			/*
			 * init setup
			 */
			String setupClass = config.getInitParameter("setup");
			if (null != setupClass) {
				Setup setup = (Setup) Class.forName(setupClass).newInstance();
				getServletContext().setAttribute(Setup.class.getName(), setup);
				setup.init(config);
			}
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	@Override
	public void destroy() {
		Nut nut = Mvc.getNut(getServletContext());
		if (null != null)
			nut.depose();
		Setup setup = (Setup) getServletContext().getAttribute(Setup.class.getName());
		if (null != setup)
			setup.destroy(getServletConfig());
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Mvc.doHttp(request, response);

	}

}
