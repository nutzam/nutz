package org.nutz.ioc.meta.fake;

import java.io.FileReader;
import java.util.Map;

import org.nutz.ioc.meta.Map2Obj;
import org.nutz.ioc.meta.Obj;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class Faking {

	@SuppressWarnings("unchecked")
	public Obj[] getObjs() {
		try {
			Map<String, Map<String, Object>> maps = (Map<String, Map<String, Object>>) Json
					.fromJson(new FileReader(Files.findFile("org/nutz/ioc/meta/objs.js")));
			Obj[] objs = new Obj[maps.size()];
			int i = 0;
			for (String name : maps.keySet()) {
				Map<String, Object> map = maps.get(name);
				map.put("name", name);
				objs[i++] = Map2Obj.parse(map);
			}
			return objs;
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
