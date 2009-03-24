package com.zzh.mvc;

import java.lang.reflect.Field;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.castor.Castors;
import com.zzh.ioc.Nut;
import com.zzh.json.JsonFormat;
import com.zzh.lang.Lang;
import com.zzh.lang.Localize;
import com.zzh.lang.Mirror;
import com.zzh.mvc.localize.Localizations;
import com.zzh.mvc.view.JsonView;

public class Mvc {

	public static <T> T getObjectAsNameValuePair(T obj, HttpServletRequest request,
			String... fieldNames) {
		if (null == obj)
			return null;
		Mirror<? extends Object> me = Mirror.me(obj.getClass());
		try {
			Field[] fields;
			if (null != fieldNames && fieldNames.length > 0) {
				fields = new Field[fieldNames.length];
				for (int i = 0; i < fields.length; i++) {
					fields[i] = me.getField(fieldNames[i]);
				}
			} else {
				fields = me.getFields();
			}
			for (Field f : fields) {
				String v = request.getParameter(f.getName());
				if (null == v)
					continue;
				v = Localize.convertAscii2Native(v, '%');
				me.setValue(obj, f, getCastors(request).castTo(v, f.getType()));
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return obj;
	}

	public static MvcSupport getMvcSupport(HttpServletRequest request) {
		ServletContext context = request.getSession().getServletContext();
		return getMvcSupport(context);
	}

	public static MvcSupport getMvcSupport(ServletContext context) {
		return (MvcSupport) context.getAttribute(Mvc.class.getName());
	}

	static void setMvcSupport(ServletContext context, MvcSupport mvc) {
		context.setAttribute(Mvc.class.getName(), mvc);
	}

	public static Castors getCastors(HttpServletRequest request) {
		return getCastors(request.getSession().getServletContext());
	}

	public static Castors getCastors(ServletContext context) {
		Castors castors = (Castors) context.getAttribute(Castors.class.getName());
		if (null == castors) {
			castors = Castors.me();
			context.setAttribute(Castors.class.getName(), castors);
		}
		return castors;
	}

	public static Nut getNut(ServletContext context) {
		return (Nut) context.getAttribute(Nut.class.getName());
	}

	public static Nut getNut(HttpServletRequest request) {
		return getNut(request.getSession().getServletContext());
	}

	public static void doHttp(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getServletPath();
		path = path.substring(0, path.lastIndexOf('.'));
		MvcSupport mvc = getMvcSupport(request);

		Map<String, String> lz = Localizations.getLocalization(request.getSession());
		request.setAttribute("msg", lz);

		Url url = null;
		Object obj;
		try {
			url = mvc.getUrl(path);
			// Do Controllors
			obj = null;
			Controllor[] cs = url.getControllors();
			if (null != cs)
				for (Controllor c : cs) {
					obj = c.execute(request, response);
					// Action Chain
					while (obj instanceof Controllor) {
						obj = ((Controllor) obj).execute(request, response);
					}
					if (obj instanceof View) {
						((View) obj).render(request, response, null);
						return;
					}
				}
			if (obj instanceof Return && !((Return) obj).isSuccess()) {
				renderFail(request, response, url, obj);
			} else {
				renderSuccess(request, response, url, obj);
			}
		} catch (Exception e) {
			obj = Return.fail(e.getMessage());
			renderFail(request, response, url, obj);
		}
	}

	private static View getDefaultView(HttpServletRequest request) {
		try {
			return getNut(request).getObject(View.class, "$default-view");
		} catch (Exception e1) {
			return new JsonView(JsonFormat.nice());
		}
	}

	private static void renderSuccess(HttpServletRequest request, HttpServletResponse response,
			Url url, Object obj) throws Exception {
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
		} catch (Exception e1) {
			throw Lang.wrapThrow(e1);
		}
	}
}
