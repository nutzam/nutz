package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Times.TmInfo;

public class TmInfo2Long extends Castor<TmInfo, Long> {

    @Override
    public Long cast(TmInfo src, Class<?> toType, String... args) throws FailToCastObjectException {
        if (null != src)
            return (long) src.valueInMillisecond;
        return null;
    }

}
