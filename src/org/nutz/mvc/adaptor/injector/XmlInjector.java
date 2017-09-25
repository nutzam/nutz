package org.nutz.mvc.adaptor.injector;

import org.nutz.mapl.Mapl;
import org.nutz.mvc.adaptor.ParamInjector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.Map;

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

    @SuppressWarnings("unchecked")
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        if (null == name)
            return Mapl.maplistToObj(refer, type);

        Map<String, Object> map = (Map<String, Object>) refer;
        Object theObj = map.get(name);
        if (null == theObj)
            return null;
        return Mapl.maplistToObj(map, type);
    }

}