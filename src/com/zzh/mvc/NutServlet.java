package com.zzh.mvc;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.Const;
import com.zzh.ioc.Ioc;
import com.zzh.ioc.MappingLoader;
import com.zzh.ioc.Nut;
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
			getServletContext().setAttribute(ServletConfig.class.getName(), config);
			initSessionCallback(config, Const.WHEN_SESSION_START);
			initSessionCallback(config, Const.WHEN_SESSION_END);
			/*
			 * eval MappingLoader
			 */
			MappingLoader loader = null;
			if (!Strings.isBlank(config.getInitParameter(Const.MVC_BY_JSON))) {
				String[] jsons = Strings.splitIgnoreBlank(config
						.getInitParameter(Const.MVC_BY_JSON));
				loader = new JsonMappingLoader(jsons);
			} else if (!Strings.isBlank(config.getInitParameter(Const.MVC_BY_DB))) {
				throw Lang.makeThrow("This feature will coming soonly :P");
			} else {
				throw Lang.makeThrow("You must setup one of parameters: '%s' | '%s'",
						Const.MVC_BY_JSON, Const.MVC_BY_DB);
			}
			/*
			 * Store Ioc If "ioc-in-session" param is true, just store
			 * MappingLoader to context attribute, else create a Ioc object and
			 * store it.
			 */
			boolean iocInSession = false;
			try {
				iocInSession = Boolean.parseBoolean(config.getInitParameter(Const.IOC_IN_SESSION));
			} catch (Exception e) {}
			if (iocInSession) {
				getServletContext().setAttribute(Ioc.class.getName(), loader);
			} else {
				Ioc ioc = new Nut(new MvcMappingLoader(loader));
				getServletContext().setAttribute(Ioc.class.getName(), ioc);
			}
			/*
			 * init local
			 */
			String msgDirPath = config.getInitParameter(Const.MSG_DIR);
			if (null != msgDirPath) {
				File msgDir = Files.findFile(msgDirPath);
				if (null == msgDir) {
					throw Lang.makeThrow("Error msg dir : '%s'", msgDirPath);
				} else if (!msgDir.exists()) {
					msgDir = new File(config.getServletContext().getRealPath(msgDirPath));
				}
				if (null == msgDir || !msgDir.exists())
					throw Lang.makeThrow("Can not access message file directory '%s'", msgDirPath);
				String msgSuffix = config.getInitParameter(Const.MSG_SUFFIX);
				Localizations.init(config.getServletContext(), msgDir, msgSuffix);
			}
			/*
			 * init setup
			 */
			String setupClass = config.getInitParameter(Const.SETUP);
			if (null != setupClass) {
				Setup setup = (Setup) Class.forName(setupClass).newInstance();
				getServletContext().setAttribute(Setup.class.getName(), setup);
				setup.init(config);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void initSessionCallback(ServletConfig config, String callbackName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = config.getInitParameter(callbackName);
		if (!Strings.isBlank(className)) {
			config.getServletContext().setAttribute(callbackName,
					Class.forName(className).newInstance());
		}
	}

	@Override
	public void destroy() {
		Setup setup = (Setup) getServletContext().getAttribute(Setup.class.getName());
		if (null != setup)
			setup.destroy(getServletConfig());
		Ioc ioc = Mvc.ioc(getServletContext());
		if (null != ioc)
			ioc.depose();
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Mvc.doHttp(request, response);

	}

}
