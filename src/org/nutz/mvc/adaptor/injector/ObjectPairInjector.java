package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.inject.Injecting;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.annotation.Param;

public class ObjectPairInjector implements ParamInjector {

	private Injecting[] injs;
	private String[] names;
	private Mirror<?> mirror;

	public ObjectPairInjector(String prefix, Class<?> type) {
		prefix = Strings.isBlank(prefix) ? "" : Strings.trim(prefix);
		this.mirror = Mirror.me(type);
		Field[] fields = mirror.getFields();
		this.injs = new Injecting[fields.length];
		this.names = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			this.injs[i] = mirror.getInjecting(f.getName());
			Param param = f.getAnnotation(Param.class);
			String nm = null == param ? f.getName() : param.value();
			this.names[i] = prefix + nm;

		}
	}

	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		Object obj = mirror.born();
		for (int i = 0; i < injs.length; i++) {
			Injecting inj = injs[i];
			String s = req.getParameter(names[i]);
			inj.inject(obj, s);
		}
		return obj;
	}
}
