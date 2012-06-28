package org.nutz.mvc.adaptor.convertor;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;
import org.nutz.mvc.adaptor.ParamConvertor;

public class DateParamConvertor implements ParamConvertor {

    private Class<?> type;

    public DateParamConvertor(Class<?> type) {
        this.type = type;
    }

    public Object convert(String[] ss) {
        if (null == ss || ss.length == 0)
            return null;

        if (Strings.isBlank(ss[0]))
            return null;

        return Castors.me().cast(ss[0], String.class, type);
    }

}
