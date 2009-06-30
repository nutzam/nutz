package org.nutz.mvc;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.NUT;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.JsonLoader;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.localize.Localizations;

@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			getServletContext().setAttribute(ServletConfig.class.getName(), config);
			initSessionCallback(config, NUT.WHEN_SESSION_START);
			initSessionCallback(config, NUT.WHEN_SESSION_END);
			/*
			 * eval MappingLoader
			 */
			ObjLoader loader = null;
			if (!Strings.isBlank(config.getInitParameter(NUT.MVC_BY_JSON))) {
				String[] jsons = Strings.splitIgnoreBlank(config
						.getInitParameter(NUT.MVC_BY_JSON));
				loader = new JsonLoader(jsons);
			} else if (!Strings.isBlank(config.getInitParameter(NUT.MVC_BY_DB))) {
				throw Lang.makeThrow("This feature will coming soonly :P");
			} else {
				throw Lang.makeThrow("You must setup one of parameters: '%s' | '%s'",
						NUT.MVC_BY_JSON, NUT.MVC_BY_DB);
			}
			/*
			 * Store Ioc If "ioc-in-session" param is true, just store
			 * MappingLoader to context attribute, else create a Ioc object and
			 * store it.
			 */
			boolean iocInSession = false;
			try {
				iocInSession = Boolean.parseBoolean(config.getInitParameter(NUT.IOC_IN_SESSION));
			} catch (Exception e) {}
			if (iocInSession) {
				getServletContext().setAttribute(Ioc.class.getName(), loader);
			} else {
				Ioc ioc = new NutIoc(new MvcMappingLoader(loader));
				getServletContext().setAttribute(Ioc.class.getName(), ioc);
			}
			/*
			 * init local
			 */
			String msgDirPath = config.getInitParameter(NUT.MSG_DIR);
			if (null != msgDirPath) {
				File msgDir = Files.findFile(msgDirPath);
				if (null == msgDir) {
					throw Lang.makeThrow("Error msg dir : '%s'", msgDirPath);
				} else if (!msgDir.exists()) {
					msgDir = new File(config.getServletContext().getRealPath(msgDirPath));
				}
				if (null == msgDir || !msgDir.exists())
					throw Lang.makeThrow("Can not access message file directory '%s'", msgDirPath);
				String msgSuffix = config.getInitParameter(NUT.MSG_SUFFIX);
				if (Strings.isBlank(msgSuffix))
					msgSuffix = NUT.MSG_SUFFIX_DEFAULT;
				Localizations.init(config.getServletContext(), msgDir, msgSuffix);
			}
			/*
			 * init setup
			 */
			String setupClass = config.getInitParameter(NUT.SETUP);
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
