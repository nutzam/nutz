package org.nutz.ioc.json;

import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.pojo.Animal;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

class Utils {

	@SuppressWarnings("unchecked")
	static Ioc I(String... ss) {
		String json = "{";
		json += Lang.concatBy(',', ss);
		json += "}";
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json
				.fromJson(json);
		return new NutIoc(new MapLoader(map));
	}

	static String J(String name, String s) {
		return name + " : {" + s + "}";
	}

	static Animal A(String s) {
		Ioc ioc = I(J("obj", s));
		return ioc.get(Animal.class, "obj");
	}

}
