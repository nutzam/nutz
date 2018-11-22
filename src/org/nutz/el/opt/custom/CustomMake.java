package org.nutz.el.opt.custom;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.conf.NutConf;
import org.nutz.el.ElException;
import org.nutz.el.opt.RunMethod;
import org.nutz.lang.Mirror;
import org.nutz.plugin.PluginManager;
import org.nutz.plugin.SimplePluginManager;

/**
 * 自定义函数工厂类
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class CustomMake {
    
    protected Map<String, RunMethod> runms = new HashMap<String, RunMethod>();
    
    protected static CustomMake me = new CustomMake();
    
    static {
        me.init();
    }
    
    /**
     * 加载插件
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CustomMake init(){
        List<String> plug = (List<String>) ((Map)NutConf.get("EL")).get("custom");
        String [] t = plug.toArray(new String[0]);
        PluginManager<RunMethod> rm = new SimplePluginManager<RunMethod>(t);
        for(RunMethod r : rm.gets()){
            me().runms.put(r.fetchSelf(), r);
        }
        return this;
    }
    
    /**
     * 自定义方法 工厂方法
     */
    public RunMethod make(String val) {
        return runms.get(val);
    }
    
    public static CustomMake me() {
        return me;
    }
    
    public void register(String name, RunMethod run) {
        runms.put(name, run);
    }
    
    public void register(String name, Method method) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new ElException("must be static method");
        }
        runms.put(name, new StaticMethodRunMethod(method));
    }
    
    public static class StaticMethodRunMethod implements RunMethod {
        
        protected Method method;
        
        public StaticMethodRunMethod(Method method) {
            this.method = method;
        }
        
        public Object run(List<Object> fetchParam) {
            return Mirror.me(method.getDeclaringClass()).invoke(null, method.getName(), fetchParam.toArray());
        }
        
        public String fetchSelf() {
            return "custom method invoke";
        }
    }
}
