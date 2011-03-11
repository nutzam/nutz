package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

	private Type type;
	private String name;

	public JsonInjector(Type type, String name) {
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
		Class<?> clazz = null;
		if(type instanceof ParameterizedType){
			clazz = (Class<?>) ((ParameterizedType)type).getRawType();
		}
		clazz = (Class<?>) type;
		return Castors.me().castTo(theObj, clazz);
	}

}
