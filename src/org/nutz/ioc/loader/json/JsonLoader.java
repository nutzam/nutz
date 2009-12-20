package org.nutz.ioc.loader.json;

import java.io.File;
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
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;

/**
 * 从 Json 文件中读取配置信息。 支持 Merge with parent ，利用 MapLoader
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@SuppressWarnings("unchecked")
public class JsonLoader extends MapLoader {

	public JsonLoader(Reader reader) {
		String s = Lang.readAll(reader);
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
		this.setMap(map);
	}

	public JsonLoader(String... files) {
		Map map = new HashMap();
		Map vars = (Map) map.get("$vars");
		for (String path : files) {
			File f = Files.findFile(path);
			if (null == f)
				throw Lang.makeThrow("Fail to find file '%s'!", path);
			map.putAll(buildMap(f, vars));
		}
		this.setMap((Map<String, Map<String, Object>>) map);
	}

	private Map buildMap(File f, Map vars) {
		String s = Files.read(f);
		Map map;
		try {
			map = Json.fromJson(Map.class, s);
		} catch (JsonException e) {
			throw new IocException(e, "Json file '%s' Error!", f);
		}
		// Get Vars
		Map theVars = evalVars(vars, map);
		// Apply Vars
		Segment seg = new CharSegment(s);
		seg.setBy(theVars);
		map = Json.fromJson(Map.class, seg.toString());
		// Apply imports
		List<String> imports = (List<String>) map.get("$imports");
		if (null != imports) {
			for (String imp : imports) {
				File imf = Files.findFile(imp);
				if (null == imf)
					imf = new File(f.getParent() + "/" + imp);
				if (!imf.exists())
					throw Lang.makeThrow("Fail to find file '%s' $import from '%s'", imp, f
							.getAbsolutePath());
				Map immap = buildMap(imf, theVars);
				map.putAll(immap);
			}
		}
		map.remove("$imports");
		map.remove("$vars");
		return map;
	}

	private Map evalVars(Map vars, Map map) {
		Map myVars = (Map) map.get("$vars");
		Map theVars = new HashMap();
		if (null != vars)
			theVars.putAll(vars);
		if (null != myVars)
			theVars.putAll(myVars);
		return theVars;
	}
}
