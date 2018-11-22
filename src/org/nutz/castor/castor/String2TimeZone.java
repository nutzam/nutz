package org.nutz.castor.castor;

import java.util.TimeZone;

import org.nutz.castor.Castor;
import org.nutz.lang.Strings;

public class String2TimeZone extends Castor<String, TimeZone> {

    @Override
    public TimeZone cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        return TimeZone.getTimeZone(src);
    }

}
