package com.zzh.mvc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.lang.Lang;
import com.zzh.lang.Strings;
import com.zzh.lang.types.CastorSetting;
import com.zzh.lang.types.Castors;

public class NutServlet extends HttpServlet {

	private static final long serialVersionUID = 3898593780912444929L;

	private MvcSupport mvc;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// Initialize MvcSupport
		try {
			// Setup server attribute name
			String serverAttName = config.getInitParameter("server-att-name");
			if (Strings.isBlank(serverAttName))
				config.getServletContext().setAttribute("server", new Server(config));
			else
				config.getServletContext().setAttribute(serverAttName, new Server(config));
			// Setup MVC
			MvcSetup setup = (MvcSetup) Class.forName(config.getInitParameter("setup-by"))
					.newInstance();
			mvc = setup.init(config);
			// Setup Castors
			String castorsSettingClassName = config.getInitParameter("castors-setting");
			Castors castors;
			if (Strings.isBlank(castorsSettingClassName)) {
				castors = Castors.me();
			} else {
				try {
					castors = Castors.me((CastorSetting) Class.forName(castorsSettingClassName)
							.newInstance());
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
			this.getServletContext().setAttribute(Castors.class.getName(), castors);
			// store MvcSupport
			getServletContext().setAttribute(MvcSupport.class.getName(), mvc);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MvcUtils.doHttp(request, response);

	}

}
