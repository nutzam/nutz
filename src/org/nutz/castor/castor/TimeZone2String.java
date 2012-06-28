package org.nutz.castor.castor;

import java.util.TimeZone;

import org.nutz.castor.Castor;

public class TimeZone2String extends Castor<TimeZone, String> {

    @Override
    public String cast(TimeZone src, Class<?> toType, String... args) {
        return src.getID();
    }

}
