package org.nutz.ioc.loader.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.resource.NutResource;
import org.nutz.resource.impl.ResourceScanHelper;

/**
 * 从 Json 文件中读取配置信息。 支持 Merge with parent ，利用 MapLoader
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@SuppressWarnings("unchecked")
public class JsonLoader extends MapLoader {

	public JsonLoader(Reader reader) {
		loadFromReader(reader);
	}

	public JsonLoader(String... paths) {
		this.setMap(new HashMap<String, Map<String, Object>>());
		// 解析路径
		for (String path : paths) {
			List<NutResource> nResources = ResourceScanHelper.scanFiles(path, ".js");
				for (NutResource nutResource : nResources) {
					try {
						loadFromReader(new InputStreamReader(	nutResource.getInputStream(),
																Encoding.CHARSET_UTF8));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			// 如果找不到?
			if (nResources.size() < 1)
				throw Lang.makeThrow(	RuntimeException.class,
										"Js folder or file no found !! Path = %s",
										path);
		}
	}

	private void loadFromReader(Reader reader) {
		String s = Lang.readAll(reader);
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
		if (null != map && map.size() > 0)
			this.getMap().putAll(map);
	}

}
