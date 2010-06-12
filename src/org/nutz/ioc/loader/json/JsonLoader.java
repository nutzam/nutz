package org.nutz.ioc.loader.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.IocException;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Resources;

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

	public JsonLoader(String... paths) throws FileNotFoundException {
		this.setMap(new HashMap<String, Map<String,Object>>());
		File[] fs = new File[paths.length];
		// 解析路径
		for (int i = 0; i < fs.length; i++) {
			fs[i] = Files.findFile(paths[i]);
			boolean flag = true;
			if (null != fs[i]) {
				// 如果是目录，读取内部所有 .json 和 .js 文件
				if (fs[i].isDirectory())
					loadFromDir(fs[i]);
				else
					loadFromReader(Streams.fileInr(paths[i]));
				flag = false;
			} else {//在jar中查找
				String pathRegex = paths[i].replace('\\', '/').replace(".", "\\.");
				if (pathRegex.endsWith("/"))
					pathRegex += ".+\\.(js|json)$";
				List<InputStream> entries = Resources.findZipEntryInClassPath(pathRegex);
				if (entries.size() > 0) {
					for (InputStream inputStream : entries)
						loadFromReader(new InputStreamReader(inputStream));
					flag = false;
				}
			}
			//如果找不到?
			if (flag)
				throw Lang.makeThrow(FileNotFoundException.class, "Js folder or file no found !! Path = %s", paths[i]);
		}
		
	}
	
	private void loadFromReader(Reader reader){
		String s = Lang.readAll(reader);
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
		if (null != map && map.size() > 0)
			this.getMap().putAll(map);
	}

	private void load(File[] files) {
		for (File f : files) {
			if (f.isFile()) {
				Map<String, Map<String, Object>> fileMap = buildMap(f);
				if (null != fileMap && fileMap.size() > 0)
					getMap().putAll(fileMap);
			}
		}
	}

	private void loadFromDir(File dir) {
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".js") || name.endsWith(".json"))
					return true;
				return false;
			}
		});
		if (files.length > 0)
			load(files);
	}

	private Map<String, Map<String, Object>> buildMap(File f) {
		try {
			return Json.fromJson(Map.class, Files.read(f));
		}
		catch (JsonException e) {
			throw new IocException(e, "Json file '%s' Error!", f);
		}
	}
}
