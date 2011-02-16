package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.inject.Injecting;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.annotation.Param;

public class ObjectPairInjector implements ParamInjector {

	protected Injecting[] injs;
	protected String[] names;
	protected Mirror<?> mirror;
	protected Field[] fields;

	public ObjectPairInjector(String prefix, Class<?> type) {
		prefix = Strings.isBlank(prefix) ? "" : Strings.trim(prefix);
		this.mirror = Mirror.me(type);
		fields = mirror.getFields();
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

	public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
		Object obj = mirror.born();
		for (int i = 0; i < injs.length; i++) {
			String[] ss = filterParam(req.getParameterValues(names[i]), i);
			if (null == ss)
				continue;
			injs[i].inject(obj, ss);
		}
		return obj;
	}
	
	/**
	 * 过滤参数
	 * @param ss
	 * @param i
	 * @return
	 */
	private String[] filterParam(String[] ss, int i){
		if(null == ss){
			return null;
		}
		if(fields[i].getType().isAssignableFrom(Date.class)){
			List<String> ts = new ArrayList<String>();
			for(String s : ss ){
				if(s == null || "".equals(s) || "".equals(Strings.trim(s))){
					continue;
				}
				ts.add(s);
			}
			return ts.toArray(new String[0]);
		}
		return ss;
	}
}


