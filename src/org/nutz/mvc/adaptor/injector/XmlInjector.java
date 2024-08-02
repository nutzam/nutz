package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Type;
import java.util.Map;

import org.nutz.mapl.Mapl;
import org.nutz.mvc.adaptor.ParamInjector;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 假设 refer 是 Map<String,Object>
 *
 * @author howe(howechiang@gmail.com)
 */
public class XmlInjector implements ParamInjector {

    private Type type;
    private String name;

    public XmlInjector(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        if (null == name) {
            return Mapl.maplistToObj(refer, type);
        }

        Map<String, Object> map = (Map<String, Object>) refer;
        Object theObj = map.get(name);
        if (null == theObj) {
            return null;
        }
        return Mapl.maplistToObj(map, type);
    }

}
