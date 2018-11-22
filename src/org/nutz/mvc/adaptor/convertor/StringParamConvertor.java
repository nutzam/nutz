package org.nutz.mvc.adaptor.convertor;

import org.nutz.mvc.adaptor.ParamConvertor;

public class StringParamConvertor implements ParamConvertor {

    public Object convert(String[] ss) {
        if (null == ss || ss.length == 0)
            return null;
        if (ss.length == 1)
            return ss[0];
        StringBuilder sb = new StringBuilder(ss[0]);
        for (int i = 1; i < ss.length; i++)
            sb.append(',').append(ss[i]);
        return sb.toString();
    }

}
