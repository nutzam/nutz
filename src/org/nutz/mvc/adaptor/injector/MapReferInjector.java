package org.nutz.mvc.adaptor.injector;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Strings;
import org.nutz.lang.inject.Injecting;

/**
 * 为PairUploadAdaptor提供支持
 * 当get方法的refer为Map时，使用Map的key获取值进行注入
 * 
 * @author lAndRaxeE(landraxee@gmail.com)
 *
 */
public class MapReferInjector extends ObjectPairInjector {

    public MapReferInjector(String prefix, Class<?> type) {
        super(prefix, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(ServletContext sc, HttpServletRequest req,
            HttpServletResponse resp, Object refer) {
        Object obj = mirror.born();
        Map<String, Object> map = null;
        if (Map.class.isAssignableFrom(refer.getClass()))
            map = (Map<String, Object>) refer;
        for (int i = 0; i < injs.length; i++) {
            Injecting inj = injs[i];
            Object s;
            if (null != map && map.containsKey(names[i]))
                s = map.get(names[i]);
            else
                s = req.getParameter(names[i]);
            if (null == s)
                continue;
            if (s instanceof String && Strings.isBlank((String) s))
                s = null;
            inj.inject(obj, s);
        }
        return obj;
    }

}
