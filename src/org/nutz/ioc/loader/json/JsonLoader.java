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
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
@SuppressWarnings("unchecked")
public class JsonLoader extends MapLoader {
    
    private static final Log log = Logs.get();
    
    protected String[] paths;
    
    /**
     * 供子类继承用.
     */
    protected JsonLoader(){}

    public JsonLoader(Reader reader) {
        loadFromReader(reader);
        if(log.isDebugEnabled())
            log.debugf("Loaded %d bean define from reader --\n%s", getMap().size(), getMap().keySet());
    }

    public JsonLoader(String... paths) {
        this.setMap(new HashMap<String, Map<String, Object>>());
        List<NutResource> list = Scans.me().loadResource("^(.+[.])(js|json)$", paths);
        try {
            for (NutResource nr : list) {
                if (log.isDebugEnabled())
                    log.debugf("loading [%s]", nr.getName());
                loadFromReader(nr.getReader());
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        if(log.isDebugEnabled())
            log.debugf("Loaded %d bean define from path=%s --> %s", getMap().size(), Arrays.toString(paths), getMap().keySet());
        this.paths = paths;
    }

    protected void loadFromReader(Reader reader) {
        String s = Lang.readAll(reader);
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) Json.fromJson(s);
        if (null != map && map.size() > 0)
            getMap().putAll(map);
    }

    public String toString() {
    	if (paths == null)
    		return super.toString();
    	return "/*" + getClass().getSimpleName() + Arrays.toString(paths) + "*/\n" + Json.toJson(map);
    }
    
    public String[] getPaths() {
        return paths;
    }
}
