package org.nutz.mvc.adaptor.convertor;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.adaptor.ParamConvertor;

public class BooleanParamConvertor implements ParamConvertor {

    public Object convert(String[] ss) {
        if (ss == null || ss.length == 0)
            return null;
        if (Strings.isBlank(ss[0]))
            return null;
        return Lang.parseBoolean(ss[0]);
    }

}
