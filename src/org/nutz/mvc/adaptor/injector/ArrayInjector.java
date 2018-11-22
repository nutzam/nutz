package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

public class ArrayInjector extends NameInjector {

    private boolean auto_split;

    public ArrayInjector(String name,
                         String datefmt,
                         Type type,
                         Type[] paramTypes,
                         String defaultValue,
                         boolean auto_split) {
        super(name, datefmt, type, paramTypes, defaultValue);
        this.auto_split = auto_split;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        Object value = null;
        if (null != refer) {
            if (refer instanceof Map) {
                value = ((Map<String, Object>) refer).get(name);
                if (value != null && value.getClass().isArray())
                    return Lang.array2array(value, klass.getComponentType());
            }
            if (value != null)
                return convertMe(value);
        }

        String[] values = req.getParameterValues(name);
        if (null == values || values.length == 0)
            return null;

        if (values.length == 1 && auto_split) {
            // 如果只有一个值，那么试图直接转换
            return convertMe(values[0]);
        }
        return Lang.array2array(values, klass.getComponentType());
    }

    protected Object convertMe(Object value) {
        try {
            return Castors.me().castTo(value, klass);
        }
        // zzh: 如果不成，按数组转换
        catch (Exception e) {
            Object re = Array.newInstance(klass.getComponentType(), 1);
            Object v = Castors.me().castTo(value, klass.getComponentType());
            Array.set(re, 0, v);
            return re;
        }
    }
}
