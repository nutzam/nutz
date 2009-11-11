package org.nutz.ioc.loader.json;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * 从 Json 文件中读取配置信息。 支持 Merge with parent ，利用 MapLoader
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonLoader extends MapLoader {

	@SuppressWarnings("unchecked")
	public JsonLoader(String... files) {
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
		for (String path : files) {
			String s = Lang.readAll(Streams.fileInr(path));
			map.putAll((Map<String, Map<String, Object>>) Json.fromJson(Map.class, s));
		}
		this.setMap(map);
	}

}
