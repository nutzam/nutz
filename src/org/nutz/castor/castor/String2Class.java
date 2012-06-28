package org.nutz.castor.castor;

import java.util.HashMap;
import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

import static java.lang.String.*;

@SuppressWarnings({"rawtypes"})
public class String2Class extends Castor<String, Class> {

    public String2Class() {
        fromClass = String.class;
        toClass = Class.class;
    }

    public static final Map<String, Class<?>> map = new HashMap<String, Class<?>>();
    static {
        map.put("long", long.class);
        map.put("int", int.class);
        map.put("short", short.class);
        map.put("byte", byte.class);
        map.put("float", float.class);
        map.put("double", double.class);
        map.put("char", char.class);
        map.put("boolean", boolean.class);
    }

    @Override
    public Class<?> cast(String src, Class toType, String... args) {
        if (null == src)
            return null;
        Class<?> c = map.get(src);
        if (null != c)
            return c;
        try {
            return Class.forName(src);
        }
        catch (ClassNotFoundException e) {
            throw new FailToCastObjectException(format("String '%s' can not cast to Class<?>!", src));
        }
    }

}
