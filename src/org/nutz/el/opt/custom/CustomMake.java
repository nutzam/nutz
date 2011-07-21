package org.nutz.el.opt.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.el.opt.RunMethod;
import org.nutz.json.Json;
import org.nutz.plugin.PluginManager;
import org.nutz.plugin.SimplePluginManager;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

/**
 * 自定义函数工厂类
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CustomMake {
    private static final String PLUGIN_PATH = "org/nutz/el/opt/custom/plugin.js";
    private static final String PLUGIN_TYPE = ".*[.]js";
    private static Map<String, RunMethod> runms = new HashMap<String, RunMethod>();
    static{
        init();
    }
    
    /**
     * 加载插件
     */
    @SuppressWarnings("unchecked")
    private static void init(){
        List<NutResource> nrs = Scans.me().scan(PLUGIN_PATH, PLUGIN_TYPE);
        for(NutResource nr : nrs){
            try {
                Map<String, List<String>> ms = (Map<String, List<String>>) Json.fromJson(nr.getReader());
                List<String> plug = ms.get("custom");
                String [] t = plug.toArray(new String[0]);
                PluginManager<RunMethod> rm = new SimplePluginManager<RunMethod>(t);
                for(RunMethod r : rm.gets()){
                    runms.put(r.fetchSelf(), r);
                }
            } catch (Exception e) {}
        }
    }
    
	/**
	 * 自定义方法 工厂方法
	 */
	public static RunMethod make(String val) {
	    return runms.get(val);
	}
	
}
