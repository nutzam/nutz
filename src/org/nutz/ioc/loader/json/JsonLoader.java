package org.nutz.ioc.loader.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
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
		List<InputStream> list = Scans.me().loadResource("^(.+[.])(js|json)$", paths);
		for (InputStream ins : list) 
			loadFromInputStream(ins);
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
