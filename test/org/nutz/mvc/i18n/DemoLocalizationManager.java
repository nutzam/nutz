package org.nutz.mvc.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.mvc.impl.NutMessageMap;

/**
 * LocalizationManager的参考实现.
 * 可以在MainSetup.init方法内, 通过Mvcs.setLocalizationManager(ioc.get(MyLocalizationManager.class))设置默认实例.
 * @author wendal
 *
 */
public class DemoLocalizationManager implements LocalizationManager {
    
    protected String defaultLocal;
    
    protected Map<String, NutMessageMap> msgs = new HashMap<String, NutMessageMap>();

    public void setDefaultLocal(String local) {
        this.defaultLocal = local;
    }

    public String getDefaultLocal() {
        return defaultLocal;
    }

    public Set<String> getLocals() {
        return msgs.keySet();
    }

    // 如果要动态替换msg, 例如从数据库读取
    // 请实现一个NutMessageMap的子类, 覆盖其get方法, 替换为动态实现
    public NutMessageMap getMessageMap(String local) {
        return msgs.get(local);
    }

    public String getMessage(String local, String key) {
        NutMessageMap map = getMessageMap(local);
        if (defaultLocal != null && map == null) {
            map = getMessageMap(defaultLocal);
        }
        if (map == null)
            return key;
        return (String) map.getOrDefault(key, key);
    }
}
