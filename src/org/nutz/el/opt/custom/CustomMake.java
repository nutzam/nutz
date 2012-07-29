package org.nutz.el.opt.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.conf.NutConf;
import org.nutz.el.opt.RunMethod;
import org.nutz.plugin.PluginManager;
import org.nutz.plugin.SimplePluginManager;

/**
 * 自定义函数工厂类
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CustomMake {
    private static Map<String, RunMethod> runms = new HashMap<String, RunMethod>();
    static{
        init();
    }
    
    /**
     * 加载插件
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void init(){
        List<String> plug = (List<String>) ((Map)NutConf.get("EL")).get("custom");
        String [] t = plug.toArray(new String[0]);
        PluginManager<RunMethod> rm = new SimplePluginManager<RunMethod>(t);
        for(RunMethod r : rm.gets()){
            runms.put(r.fetchSelf(), r);
        }
    }
    
    /**
     * 自定义方法 工厂方法
     */
    public static RunMethod make(String val) {
        return runms.get(val);
    }
    
}
