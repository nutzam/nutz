package org.nutz.ioc.loader.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.meta.IocObject;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mapl.Mapl;
import org.nutz.mvc.adaptor.ParamExtractor;
import org.nutz.mvc.adaptor.Params;
import org.nutz.mvc.adaptor.injector.ObjectNaviNode;

public class PropertiesIocLoader extends PropertiesProxy implements IocLoader {

    private static Log log = Logs.get();
    
    protected Map<String, IocObject> objs = new HashMap<String, IocObject>();
    
    public PropertiesIocLoader() {}
    
    public PropertiesIocLoader(String...paths) {
        super(paths);
        log.debug("beans = " + objs.keySet());
    }

    @Override
    public String[] getName() {
        reload();
        return objs.keySet().toArray(new String[objs.size()]);
    }

    @Override
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        reload();
        return objs.get(name);
    }

    @Override
    public boolean has(String name) {
        reload();
        return objs.containsKey(name);
    }
    
    @SuppressWarnings("rawtypes")
    public void reload() {
        List<String> beanNames = new ArrayList<String>();
        for (String key : keys()) {
            if (!key.startsWith("ioc.") || key.length() < 5) {
                continue;
            }
            String[] tmp = key.split("[.]");
            if (tmp.length == 3) {
                if ("type".equals(tmp[2]) || "factory".equals(tmp[2])) {
                    beanNames.add(tmp[1]);
                }
            }
        }
        for (String beanName : beanNames) {
            ObjectNaviNode no = new ObjectNaviNode();
            String prefix = "ioc." + beanName+".";
            String pre = "";
            ParamExtractor pe = Params.makeParamExtractor(null, this.toMap());
            for (Object name : pe.keys()) {
                String na = (String) name;
                if (na.startsWith(prefix)) {
                    no.put(pre + na, pe.extractor(na));
                }
            }
            Object model = no.get();
            Object re = Mapl.maplistToObj(((Map)model).get(beanName), IocObject.class);
            this.objs.put(beanName, (IocObject) re);
        }
        
        // 插入自身
        //this.objs.put("conf", Iocs.wrap(this));
    }
}
