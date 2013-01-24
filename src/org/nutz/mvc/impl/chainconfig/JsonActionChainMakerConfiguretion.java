package org.nutz.mvc.impl.chainconfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 通过json文件获取配置信息.
 * <p/>默认配置会首先加载,用户文件可以覆盖之
 * @author wendal(wendal1985@gmail.com)
 */
public class JsonActionChainMakerConfiguretion implements ActionChainMakerConfiguration {

    protected Map<String,Map<String,Object>> map = new HashMap<String, Map<String,Object>>();
    
    @SuppressWarnings("unchecked")
    public JsonActionChainMakerConfiguretion(String...jsonPaths) {
        List<NutResource> list = Scans.me().loadResource("^(.+[.])(js|json)$", jsonPaths);
        try {
            map.putAll(Json.fromJson(Map.class, new InputStreamReader(getClass().getClassLoader().getResourceAsStream("org/nutz/mvc/impl/chainconfig/default-chains.js"))));
            
            for (NutResource nr : list)
                map.putAll(Json.fromJson(Map.class,nr.getReader()));
        }
        catch (IOException e) {
        	throw Lang.wrapThrow(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getProcessors(String key) {
        Map<String,Object> config = map.get(key);
        if(config != null && config.containsKey("ps"))
            return (List<String>) config.get("ps");
        return (List<String>) map.get("default").get("ps");
    }
    
    public String getErrorProcessor(String key) {
        Map<String,Object> config = map.get(key);
        if(config != null && config.containsKey("error"))
            return (String) config.get("error");
        return (String) map.get("default").get("error");
    }
    
}
