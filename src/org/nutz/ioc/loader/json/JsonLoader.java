package org.nutz.ioc.loader.json;

import java.io.File;
import java.io.FilenameFilter;
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

	public JsonLoader(String... paths) {
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
		File[] fs = new File[paths.length];
		// 解析路径
		for (int i = 0; i < fs.length; i++) {
			fs[i] = Files.findFile(paths[i]);
			if (null == fs[i])
				throw Lang.makeThrow("Fail to find file '%s'!", paths[i]);
		}
		load(map, fs);
		this.setMap(map);
	}

	private void load(Map<String, Map<String, Object>> map, File[] files) {
		for (File f : files) {
			// 如果是目录，读取内部所有 .json 和 .js 文件
			if (f.isDirectory()) {
				loadFromDir(map, f);
			}
			// 如果是文件，加载其内容
			else if (f.isFile()) {
				map.putAll(buildMap(f));
			}
		}
	}

	private void loadFromDir(Map<String, Map<String, Object>> map, File dir) {
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".js") || name.endsWith(".json"))
					return true;
				return false;
			}
		});
		load(map, files);
	}

	private Map<String, Map<String, Object>> buildMap(File f) {
		try {
			return Json.fromJson(Map.class, Files.read(f));
		} catch (JsonException e) {
			throw new IocException(e, "Json file '%s' Error!", f);
		}
	}
}
