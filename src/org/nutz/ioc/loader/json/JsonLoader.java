package org.nutz.ioc.loader.json;

import java.io.File;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.IocException;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

/**
 * 从 Json 文件中读取配置信息。 支持 Merge with parent ，利用 MapLoader
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@SuppressWarnings("unchecked")
public class JsonLoader extends MapLoader {

	public JsonLoader(Reader reader) {
		String s = Lang.readAll(reader);
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
		this.setMap(map);
	}

	public JsonLoader(String... files) {
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
		for (String path : files) {
			File f = Files.findFile(path);
			if (null == f)
				throw Lang.makeThrow("Fail to find file '%s'!", path);
			map.putAll(buildMap(f));
		}
		this.setMap(map);
	}

	private Map<String, Map<String, Object>> buildMap(File f) {
		try {
			return Json.fromJson(Map.class, Files.read(f));
		} catch (JsonException e) {
			throw new IocException(e, "Json file '%s' Error!", f);
		}
	}
}
