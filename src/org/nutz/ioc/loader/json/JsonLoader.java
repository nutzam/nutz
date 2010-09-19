package org.nutz.ioc.loader.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 从 Json 文件中读取配置信息。 支持 Merge with parent ，利用 MapLoader
 * <p>
 * 注，如果 JSON 配置文件被打入 Jar 包中，这个加载器将不能正常工作
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
		try {
			// 解析路径
			for (String path : paths) {
				File f = Files.findFile(path);
				// 如果是文件，直接加载
				if (null != f && f.isFile()) {
					loadFromInputStream(new FileInputStream(f));
					continue;
				}
				List<NutResource> rsList = Scans.me().scan(path, "^(.+[.])(js|json)$");
				for (NutResource nr : rsList) {
					loadFromInputStream(nr.getInputStream());
				}
				// 如果找不到?
				if (rsList.size() < 1)
					throw Lang.makeThrow(	RuntimeException.class,
											"Js folder or file no found !! Path = %s",
											path);
			}
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private void loadFromInputStream(InputStream ins) {
		loadFromReader(new InputStreamReader(ins, Encoding.CHARSET_UTF8));
	}

	private void loadFromReader(Reader reader) {
		String s = Lang.readAll(reader);
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
		if (null != map && map.size() > 0)
			this.getMap().putAll(map);
	}

}
