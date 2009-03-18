package com.zzh.ioc.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.ioc.Mapping;
import com.zzh.ioc.MappingLoader;
import com.zzh.json.Json;
import com.zzh.json.JsonException;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;

public class JsonMappingLoader implements MappingLoader {

	@SuppressWarnings("unchecked")
	public JsonMappingLoader(String... paths) throws JsonException, FileNotFoundException {
		ArrayList<File> files = new ArrayList<File>(100);
		for (String path : paths) {
			File f = Files.findFile(path);
			if (null == f || !f.exists())
				throw Lang.makeThrow("Can not find file '%s'", path);
			if (f.isFile())
				files.add(f);
			else if (f.isDirectory()) {
				File[] fs = f.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".josn");
					}
				});
				for (File sf : fs)
					files.add(sf);
			}
		}
		mappings = new HashMap<String, Mapping>();
		for (Iterator<File> it = files.iterator(); it.hasNext();) {
			File f = it.next();
			Map<String, Object> map = (Map<String, Object>) Json.fromJson(new InputStreamReader(
					new FileInputStream(f)));
			for (Iterator<String> ki = map.keySet().iterator(); ki.hasNext();) {
				String key = ki.next();
				Object value = map.get(key);
				Mapping mapping = new JsonMapping((Map<String, Object>) value);
				mappings.put(key, mapping);
			}
		}
	}

	private Map<String, Mapping> mappings;

	@Override
	public Mapping load(String name) {
		return mappings.get(name);
	}

}
