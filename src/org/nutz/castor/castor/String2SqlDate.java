package org.nutz.castor.castor;

import org.nutz.lang.Strings;

public class String2SqlDate extends DateTimeCastor<String, java.sql.Date> {

    @Override
    public java.sql.Date cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;

        return new java.sql.Date(toDate(src).getTime());

    }

}
