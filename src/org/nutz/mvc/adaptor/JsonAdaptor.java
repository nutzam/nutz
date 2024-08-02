package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.adaptor.injector.JsonInjector;
import org.nutz.mvc.adaptor.injector.VoidInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 假设，整个获得的输入流，是一个 JSON 字符串
 *
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class JsonAdaptor extends PairAdaptor {

    @Override
    protected ParamInjector evalInjector(Type type, Param param) {
        if (param == null || "..".equals(param.value())) {
            Class<?> clazz = Lang.getTypeClass(type);
            if (clazz != null && AdaptorErrorContext.class.isAssignableFrom(clazz)) {
                return new VoidInjector();
            }
            return new JsonInjector(type, null);
        }
        return super.evalInjector(type, param);
    }

    @Override
    public Object getReferObject(ServletContext sc,
                                 HttpServletRequest req,
                                 HttpServletResponse resp,
                                 String[] pathArgs) {
        // Read all as String
        try {
            // TODO URL传来的参数会丢失
            return Json.fromJson(Streams.utf8r(req.getInputStream()));
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}
