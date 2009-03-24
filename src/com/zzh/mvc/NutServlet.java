package com.zzh.mvc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.ioc.Deposer;
import com.zzh.ioc.Nut;
import com.zzh.ioc.MappingLoader;
import com.zzh.ioc.json.JsonMappingLoader;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;

@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// Initialize MvcSupport
		try {
			String mvcByJson = config.getInitParameter("mvc-by-json");
			MappingLoader loader = null;
			if (!Strings.isBlank(mvcByJson)) {
				loader = new JsonMappingLoader(Strings.splitIgnoreBlank(mvcByJson));
			} else {
				throw Lang.makeThrow("You must setup one of parameters: '%s' | '%s' | '%s'",
						"mvc-by-json", "mvc-by-db");
			}
			Nut nut = new Nut(new MvcMappingLoader(loader));
			// init deposer
			String[] ss = Strings.splitIgnoreBlank(config.getInitParameter("deposers"));
			if (null != ss && ss.length > 0) {
				for (String s : ss) {
					Class<?> deposerType = Class.forName(s);
					Deposer deposer = (Deposer) deposerType.newInstance();
					nut.addDeposer(deposer);
				}
			}
			// store nut
			getServletContext().setAttribute(Nut.class.getName(), nut);
			NutMvc mvc = new NutMvc(nut, config);
			Mvc.setMvcSupport(getServletContext(), mvc);
			// store MvcSupport
			getServletContext().setAttribute(MvcSupport.class.getName(), mvc);
			// init local
			
			// init setup
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
