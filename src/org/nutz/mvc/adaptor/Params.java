package org.nutz.mvc.adaptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Mirror;
import org.nutz.mvc.adaptor.convertor.ArrayParamConvertor;
import org.nutz.mvc.adaptor.convertor.BooleanParamConvertor;
import org.nutz.mvc.adaptor.convertor.DateParamConvertor;
import org.nutz.mvc.adaptor.convertor.StringParamConvertor;
import org.nutz.mvc.adaptor.extractor.BaseParamExtractor;
import org.nutz.mvc.adaptor.extractor.MapParamExtractor;

public abstract class Params {

    /**
     * 构造参数转换器
     * 
     * @param type
     *            要转换的目标类型
     * @param datefmt
     *            如果目标类型是 DateTime 类似的类型，可以声明一个特殊转换格式，<br>
     *            如果为 null 表示用 Times.D 函数自动猜测
     */
    public static ParamConvertor makeParamConvertor(Class<?> type,
                                                    String datefmt) {
        return makeParamConvertor(type, datefmt, null);
    }
    public static ParamConvertor makeParamConvertor(Class<?> type,
                                                    String datefmt,
                                                    String locale) {
        if (type.isArray())
            return new ArrayParamConvertor(type.getComponentType());

        Mirror<?> mirror = Mirror.me(type);
        if (mirror.isDateTimeLike()) {
            return new DateParamConvertor(type, datefmt, locale);
        }
        if (mirror.isBoolean()) {
            return new BooleanParamConvertor();
        }

        return new StringParamConvertor();
    }

    /**
     * 构造参数提取器
     */
    @SuppressWarnings("unchecked")
    public static ParamExtractor makeParamExtractor(HttpServletRequest req,
                                                    Object refer) {
        if (refer != null && Map.class.isAssignableFrom(refer.getClass())) {
            return new MapParamExtractor(req, (Map<String, Object>) refer);
        }
        return new BaseParamExtractor(req);
    }
}
