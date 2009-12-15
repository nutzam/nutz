package org.nutz.mvc.adaptor.injector;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

/**
 * 假设 refer 是 Map<String,Object>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonInjector implements ParamInjector {

	private Class<?> type;
	private String name;

	public JsonInjector(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Object get(HttpServletRequest req, HttpServletResponse resp, Object refer) {
		if (null == name)
			return Lang.map2Object((Map<?, ?>) refer, type);
		Object map = ((Map<?, ?>) refer).get(name);
		if (!(map instanceof Map<?, ?>))
			throw Lang.makeThrow("Wrong JSON string,  '%s' should be another map!: \n %s", name,
					Json.toJson(refer, JsonFormat.nice()));
		return Lang.map2Object((Map<?, ?>) map, type);
	}

}
