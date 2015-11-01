package org.nutz.mvc.upload.injector;

import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.injector.NameInjector;

public class MapItemInjector extends NameInjector {

    public MapItemInjector(String name,
            String datefmt,
            Type type,
            Type[] paramTypes,
            String defaultValue) {
        super(name, datefmt, type, paramTypes, defaultValue);
    }

    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        if (null != refer)
            if (refer instanceof Map<?, ?>) {
                Object value = ((Map<?, ?>) refer).get(name);
                return Castors.me().castTo(value, klass);
            }
        return null;
    }

}
