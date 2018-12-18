package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Times.TmInfo;

public class TmInfo2Integer extends Castor<TmInfo, Integer> {

    @Override
    public Integer cast(TmInfo src, Class<?> toType, String... args) {
        if (null != src) {
            return src.value;
        }
        return null;
    }

}
