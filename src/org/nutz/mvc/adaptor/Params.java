package org.nutz.mvc.adaptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Mirror;
import org.nutz.mvc.adaptor.convertor.*;
import org.nutz.mvc.adaptor.extractor.BaseParamExtractor;
import org.nutz.mvc.adaptor.extractor.MapParamExtractor;

public abstract class Params {

    /**
     * 构造参数转换器
     */
    public static ParamConvertor makeParamConvertor(Class<?> type) {
        if (type.isArray())
            return new ArrayParamConvertor(type.getComponentType());

        Mirror<?> mirror = Mirror.me(type);
        if (mirror.isDateTimeLike()) {
            return new DateParamConvertor(type);
        }

        return new StringParamConvertor();
    }

    /**
     * 构造参数提取器
     */
    @SuppressWarnings("unchecked")
    public static ParamExtractor makeParamExtractor(HttpServletRequest req, Object refer){
        if (refer != null && Map.class.isAssignableFrom(refer.getClass())){
            return new MapParamExtractor(req, (Map<String, Object>) refer);
        }
        return new BaseParamExtractor(req);
    }
}
