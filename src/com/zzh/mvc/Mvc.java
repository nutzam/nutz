package com.zzh.mvc;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.ioc.Ioc;
import com.zzh.ioc.MappingLoader;
import com.zzh.ioc.Nut;
import com.zzh.json.JsonFormat;
import com.zzh.lang.Lang;
import com.zzh.mvc.access.Session;
import com.zzh.mvc.localize.Localizations;
import com.zzh.mvc.view.JsonView;

import static java.lang.String.*;

public class Mvc {

	public static Ioc ioc(ServletContext context) {
		Object att = context.getAttribute(Ioc.class.getName());
		if (att instanceof Ioc)
			return (Ioc) att;
		else if (att instanceof MappingLoader)
			return new Nut((MappingLoader) att);
		throw new RuntimeException(format("Attribute [%s] should be '%s' or %s", Ioc.class
				.getName(), Ioc.class.getName(), MappingLoader.class));
	}

	public static Ioc ioc(HttpServletRequest request) {
		return Session.me(request).ioc();
	}

	public static void doHttp(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getServletPath();
		int lio = path.lastIndexOf('.');
		if (lio > 0)
			path = path.substring(0, lio);
		MvcSupport mvc = Session.me(request).mvc();

		Map<String, String> lz = Localizations.getLocalization(request.getSession());
		if (lz != null)
			request.setAttribute("msg", lz);

		Url url = null;
		Object obj = null;
		try {
			url = mvc.getUrl(path);
			obj = process(request, response, url.getControllors());
			if (obj instanceof Return && !((Return) obj).isSuccess()) {
				renderFail(request, response, url, obj);
			} else if (obj instanceof View) {
				((View) obj).render(request, response, null);
			} else {
				renderSuccess(request, response, url, obj);
			}
		} catch (Throwable e) {
			response.reset();
			obj = Return.fail("%s", e.getMessage());
			renderFail(request, response, url, obj);
		}
	}

	private static Object process(HttpServletRequest request, HttpServletResponse response,
			Controllor[] cs) throws Throwable {
		Object obj = null;
		if (null != cs) {
			for (Controllor c : cs) {
				obj = c.execute(request, response);
				while (obj instanceof Controllor) {
					obj = ((Controllor) obj).execute(request, response);
				}
				if (obj instanceof View) {
					return obj;
				}
			}
		}
		return obj;
	}

	private static View getDefaultView(HttpServletRequest request) {
		try {
			return ioc(request).get(View.class, "$default-view");
		} catch (Exception e1) {
			return new JsonView(JsonFormat.nice());
		}
	}

	private static void renderSuccess(HttpServletRequest request, HttpServletResponse response,
			Url url, Object obj) throws Throwable {
		View v;
		v = url.getOk();
		if (null != v)
			v.render(request, response, obj);
	}

	private static void renderFail(HttpServletRequest request, HttpServletResponse response,
			Url url, Object obj) {
		try {
			if (null != url && null != url.getError()) {
				url.getError().render(request, response, obj);
			} else {
				getDefaultView(request).render(request, response, obj);
			}
		} catch (Throwable e1) {
			throw Lang.wrapThrow(e1);
		}
	}
}
