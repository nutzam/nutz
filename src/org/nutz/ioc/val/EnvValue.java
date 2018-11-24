package org.nutz.ioc.val;

public class EnvValue extends ListableValueProxy {
    
    public EnvValue(Object obj) {
        super(obj);
    }

    protected Object getValue(String key) {
        return System.getenv(key);
    }

}
