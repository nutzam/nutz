package org.nutz.ioc.json;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.ObjLoader;
import org.nutz.ioc.meta.Map2Obj;
import org.nutz.ioc.meta.Obj;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class JsonLoader implements ObjLoader {

	public JsonLoader() {
		map = new HashMap<String, Obj>();
	}

	public JsonLoader(String... paths) {
		this();
		for (String path : paths) {
			File f = Files.findFile(path);
			if (null == f)
				throw Lang.makeThrow("Fail to find json file '%s'", path);
			try {
				Reader reader = new FileReader(f);
				load(reader);
				reader.close();
			} catch (Exception e) {
				throw Lang.makeThrow("Fail to find json file '%s' because:\n%s", path, e
						.getMessage());
			}
		}
	}

	public JsonLoader(Reader reader) {
		this();
		load(reader);
	}

	@SuppressWarnings("unchecked")
	public void load(Reader reader) {
		Map<String, Map<?, ?>> objMap = (Map<String, Map<?, ?>>) Json.fromJson(reader);
		String[] keys = new String[objMap.size()];
		int i = 0;
		for (String key : objMap.keySet()) {
			Obj obj = Map2Obj.parse(objMap.get(key));
			obj.setName(key);
			map.put(key, obj);
			keys[i++] = key;
		}
		if (null == this.keys)
			this.keys = keys;
		else
			this.keys = Lang.merge(this.keys, keys);
	}

	public JsonLoader clear() {
		map.clear();
		return this;
	}

	private Map<String, Obj> map;
	private String[] keys;

	@Override
	public String[] keys() {
		return keys;
	}

	@Override
	public Obj load(String name) {
		return map.get(name);
	}

}
