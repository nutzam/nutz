package org.nutz.mvc.impl.chainconfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.NutRuntimeException;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 通过json文件获取配置信息.
 * <p/>默认配置会首先加载,用户文件可以覆盖之
 * @author wendal(wendal1985@gmail.com)
 * TODO 合并到NutConfig
 */
public class JsonActionChainMakerConfiguretion implements ActionChainMakerConfiguration {

    private static final Log log = Logs.get();
    
    protected Map<String,Map<String,Object>> map = new HashMap<String, Map<String,Object>>();
    
    @SuppressWarnings("unchecked")
    public JsonActionChainMakerConfiguretion(String...jsonPaths) {
        List<NutResource> list = Scans.me().loadResource("^(.+[.])(js|json)$", jsonPaths);
        try {
            File defaultChainsFile = Files.findFile("org/nutz/mvc/impl/chainconfig/default-chains.js");
            if (defaultChainsFile == null) {
                log.warn("org/nutz/mvc/impl/chainconfig/default-chains.js NOT Found!!");
                throw new NutRuntimeException("Default Chains File Not FOUND?!");
            }
            map.putAll(Json.fromJsonFile(Map.class, defaultChainsFile));
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
