package org.nutz.ioc.val;

import java.util.Properties;

public class SysPropValue extends ListableValueProxy {
    
    public SysPropValue(Object obj) {
        super(obj);
    }

    public Object getValue(String key) {
        Properties properties = System.getProperties();
        if (properties != null)
            return properties.get(key);
        return null;
    }

}
