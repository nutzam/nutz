package org.nutz.ioc.impl;

import java.util.Collection;
import java.util.Map;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.ioc.val.*;
import org.nutz.lang.Lang;

public class DefaultValueProxyMaker implements ValueProxyMaker {

    @SuppressWarnings("unchecked")
    public ValueProxy make(IocMaking ing, IocValue iv) {
        Object value = iv.getValue();
        String type = iv.getType();
        // Null
        if ("null".equals(type) || null == value) {
            return new StaticValue(null);
        }
        // String, Number, .....
        else if ("normal".equals(type) || null == type) {
            // Array
            if (value.getClass().isArray()) {
                Object[] vs = (Object[]) value;
                IocValue[] tmp = new IocValue[vs.length];
                for (int i = 0; i < tmp.length; i++)
                    tmp[i] = (IocValue) vs[i];
                return new ArrayValue(ing, tmp);
            }
            // Map
            else if (value instanceof Map<?, ?>) {
                return new MapValue(ing,
                                    (Map<String, IocValue>) value,
                                    (Class<? extends Map<String, Object>>) value.getClass());
            }
            // Collection
            else if (value instanceof Collection<?>) {
                return new CollectionValue(    ing,
                                            (Collection<IocValue>) value,
                                            (Class<? extends Collection<Object>>) value.getClass());
            }
            // Inner Object
            else if (value instanceof IocObject) {
                return new InnerValue((IocObject) value);
            }
            return new StaticValue(value);
        }
        // Refer
        else if ("refer".equals(type)) {
            String s = value.toString();
            if (null != s) {
                String renm = s.toLowerCase();
                // $Ioc
                if ("$ioc".equals(renm)) {
                    return new IocSelfValue();
                }
                // $Name
                else if ("$name".equals(renm)) {
                    return new ObjectNameValue();
                }
                // $Context
                else if ("$context".equals(renm)) {
                    return new IocContextObjectValue();
                }
            }
            return new ReferValue(s);
        }
        // Java
        else if ("java".equals(type)) {
            return new JavaValue(value.toString());
        }
        // File
        else if ("file".equals(type)) {
            return new FileValue(value.toString());
        }
        // Env
        else if ("env".equals(type)) {
            return new EnvValue(value.toString());
        }
        // System Properties
        else if ("sys".equals(type)) {
            return new SysPropValue(value.toString());
        }
        // Inner
        else if ("inner".equals(type)) {
            return new InnerValue((IocObject) value);
        }
        // JNDI
        else if ("jndi".equals(type)) {
            return new JNDI_Value(value.toString());
        }
        return null;
    }

    public String[] supportedTypes() {
        return Lang.array("refer", "java", "env", "file", "sys", "jndi");
    }

}
