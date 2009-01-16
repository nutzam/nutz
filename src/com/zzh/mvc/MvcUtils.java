package com.zzh.mvc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.json.Json;
import com.zzh.lang.Mirror;
import com.zzh.lang.types.Castors;
import com.zzh.localize.Localize;
import com.zzh.mvc.c.AbstractControllor;
import com.zzh.mvc.v.JspView;

public class MvcUtils {

	public static <T> T getObjectAsNameValuePair(T obj, HttpServletRequest request) {
		Mirror<? extends Object> me = Mirror.me(obj.getClass());
		Field[] fields = me.getFields();
		for (Field f : fields) {
			String v = request.getParameter(f.getName());
			if (null == v)
				continue;
			v = Localize.convertAscii2Native(v, '%');
			try {
				Method setter = me.getSetter(f);
				Object vv = getCastors(request).castTo(v, f.getType());
				setter.invoke(obj, vv);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Castors getCastors(HttpServletRequest request) {
		return getCastors(request.getSession().getServletContext());
	}

	public static Castors getCastors(ServletContext context) {
		return (Castors) context.getAttribute(Castors.class.getName());
	}

	public static <T> T getObjectAsJson(Class<T> type, HttpServletRequest request)
			throws IOException {
		return Json.fromJson(request.getInputStream(), type);
	}

	public static MvcSupport getMvcSupport(HttpServletRequest request) {
		return (MvcSupport) request.getSession().getServletContext().getAttribute(
				MvcSupport.class.getName());
	}

	@SuppressWarnings("unchecked")
	public static void doHttp(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		String url = request.getServletPath();
		url = url.substring(0, url.lastIndexOf('.'));
		MvcSupport mvc = getMvcSupport(request);

		UrlMapping mapping = mvc.getUrlMapping(url);
		if (null == mapping) {
			throw new RuntimeException(String.format("Fail to get mapping for url '%s'!", url));
		}
		try {
			Object obj = null;
			String cName = mapping.getControllorName();
			if (null != cName) {
				Controllor c = mvc.getControllor(cName);
				if (c instanceof AbstractControllor) {
					((AbstractControllor<Object>) c).setService(mvc.getService(mapping
							.getServiceName()));
				}
				obj = c.execute(request, response);
			}

			String vName = mapping.getViewName();
			if (null != vName) {
				View v = mvc.getView(vName);
				v.setName(vName);
				/* For JSP View */
				if (v instanceof JspView) {
					if (null == ((JspView) v).getServletContext())
						((JspView) v).setServletContext(request.getSession().getServletContext());
				} /* Done for JSP view */
				v.render(request, response, obj);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
