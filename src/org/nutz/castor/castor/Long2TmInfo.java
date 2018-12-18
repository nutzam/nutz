package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Times;
import org.nutz.lang.Times.TmInfo;

public class Long2TmInfo extends Castor<Long, TmInfo> {

    @Override
    public TmInfo cast(Long src, Class<?> toType, String... args) {
        if (null != src) {
            return Times.Tims(src);
        }
        return null;
    }

}
