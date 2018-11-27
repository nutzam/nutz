package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Times;
import org.nutz.lang.Times.TmInfo;

public class String2TmInfo extends Castor<String, TmInfo> {

    @Override
    public TmInfo cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if(null!=src) {
            return Times.Ti(src);
        }
        return null;
    }

}
