package org.nutz.mvc.upload.injector;

import java.lang.reflect.Type;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.mvc.adaptor.injector.NameInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        if (null != refer) {
            if (refer instanceof Map<?, ?>) {
                Object value = ((Map<?, ?>) refer).get(name);
                return Castors.me().castTo(value, klass);
            }
        }
        return null;
    }

}
