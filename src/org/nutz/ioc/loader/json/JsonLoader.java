package org.nutz.ioc.loader.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
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
    
    private static final Log log = Logs.get();

    public JsonLoader(Reader reader) {
        loadFromReader(reader);
        if(log.isDebugEnabled())
            log.debugf("Loaded %d bean define from reader --\n%s", getMap().size(), getMap().keySet());
    }

    public JsonLoader(String... paths) {
        this.setMap(new HashMap<String, Map<String, Object>>());
        List<NutResource> list = Scans.me().loadResource("^(.+[.])(js|json)$", paths);
        try {
            for (NutResource nr : list)
                loadFromReader(nr.getReader());
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        if(log.isDebugEnabled())
            log.debugf("Loaded %d bean define from path=%s --> %s", getMap().size(), Arrays.toString(paths), getMap().keySet());
    }

    private void loadFromReader(Reader reader) {
        String s = Lang.readAll(reader);
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
        if (null != map && map.size() > 0)
            getMap().putAll(map);
    }

}
