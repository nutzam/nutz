package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.adaptor.injector.JsonInjector;
import org.nutz.mvc.annotation.Param;

/**
 * 假设，整个输入输入流，是一个 JSON 字符串
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class JsonAdaptor extends PairAdaptor {

    protected ParamInjector evalInjector(Type type, Param param) {
        if (param == null || "..".equals(param.value()))
            return new JsonInjector(type, null);
        return super.evalInjector(type, param);
    }

    public Object getReferObject(    ServletContext sc,
                            HttpServletRequest req,
                            HttpServletResponse resp, String[] pathArgs) {
        // Read all as String
        try {
            //TODO URL传来的参数会丢失
            return Json.fromJson(Streams.utf8r(req.getInputStream()));
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}
