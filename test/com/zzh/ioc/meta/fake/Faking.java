package com.zzh.ioc.meta.fake;

import java.io.FileReader;
import java.util.Map;

import com.zzh.ioc.meta.Map2Obj;
import com.zzh.ioc.meta.Obj;
import com.zzh.json.Json;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;

public class Faking {

	@SuppressWarnings("unchecked")
	public Obj[] getObjs() {
		try {
			Map<String, Map<String, Object>> maps = (Map<String, Map<String, Object>>) Json
					.fromJson(new FileReader(Files.findFile("com/zzh/ioc/meta/objs.js")));
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
