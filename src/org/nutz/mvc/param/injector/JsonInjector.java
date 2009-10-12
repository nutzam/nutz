package org.nutz.mvc.param.injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.mvc.param.ParamInjector;

public class JsonInjector implements ParamInjector {

	private Class<?> type;

	public JsonInjector(Class<?> type) {
		this.type = type;
	}

	public Object get(HttpServletRequest request, HttpServletResponse response, Object refer) {
		return Json.fromJson(type, refer.toString());
	}

}
