package org.nutz.ioc.val;

import java.util.Properties;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class SysPropValue implements ValueProxy{
    
    private String name;
    
    public SysPropValue(String name) {
        this.name = name;
    }

    public Object get(IocMaking ing) {
        Properties properties = System.getProperties();
        if (properties != null)
            return properties.get(name);
        return null;
    }

}
