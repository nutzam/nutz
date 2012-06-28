package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

/**
 * 假设 refer 是 Map<String,Object>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonInjector implements ParamInjector {

    private Type type;
    private String name;

    public JsonInjector(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        if (null == name)
            return Castors.me().castTo(refer, Lang.getTypeClass(type));

        Map<String, Object> map = (Map<String, Object>)refer;
        Object theObj = map.get(name);
        if (null == theObj)
            return null;
        Class<?> clazz = Lang.getTypeClass(type);
        return Castors.me().castTo(theObj, clazz);
    }

}
