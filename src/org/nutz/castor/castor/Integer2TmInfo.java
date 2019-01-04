package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Times;
import org.nutz.lang.Times.TmInfo;

public class Integer2TmInfo extends Castor<Integer, TmInfo> {

    @Override
    public TmInfo cast(Integer src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (null != src) {
            return Times.Ti(src);
        }
        return null;
    }

}
