package org.nutz.castor.castor;

import org.nutz.lang.Strings;

public class String2SqlTime extends DateTimeCastor<String, java.sql.Time> {

    @Override
    public java.sql.Time cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        return new java.sql.Time(toDate(src).getTime());
    }

}
