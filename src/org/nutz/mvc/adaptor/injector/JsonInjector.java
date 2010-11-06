package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
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

	public Object get(	ServletContext sc,
						HttpServletRequest req,
						HttpServletResponse resp,
						Object refer) {
		if (null == name)
			return Json.fromJson(type, refer.toString());

		NutMap map = Json.fromJson(NutMap.class, refer.toString());
		Object theObj = map.get(name);
		if (null == theObj)
			return null;
		return Castors.me().castTo(theObj, type);
	}

}
