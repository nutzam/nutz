package org.nutz.ioc.val;

import java.util.Set;

import org.nutz.el.El;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.util.SimpleContext;

public class EL_Value extends SimpleContext implements ValueProxy {
    
    protected El el;
    
    protected Ioc ioc;
    
    public EL_Value(String el) {
        this.el = new El(el);
    }

    public Object get(IocMaking ing) {
        this.ioc = ing.getIoc();
        return el.eval(this);
    }
    
    public boolean has(String key) {
        if (key == null)
            return false;
        if ("sys".equals(key))
            return true;
        if ("env".equals(key))
            return true;
        if ("$ioc".equals(key))
            return true;
        if (key.startsWith("$") && key.length() > 1)
            return ioc.has(key.substring(1));
        return super.has(key);
    }
    
    public Set<String> keys() {
        Set<String> keys = super.keys();
        keys.add("sys");
        keys.add("env");
        keys.add("$ioc");
        for (String name : ioc.getNames()) {
            keys.add("$"+name);
        }
        return keys;
    }
    
    public int size() {
        return this.keys().size();
    }
    
    public Object get(String key) {
        if (key == null)
            return null;
        if ("sys".equals(key))
            return System.getProperties();
        if ("env".equals(key))
            return System.getenv();
        if ("$ioc".equals(key))
            return ioc;
        if (key.startsWith("$") && key.length() > 1)
            return ioc.get(Object.class, key.substring(1));
        return super.get(key);
    }
}
