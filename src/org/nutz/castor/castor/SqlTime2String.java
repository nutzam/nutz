package org.nutz.castor.castor;

import java.text.DateFormat;

public class SqlTime2String extends DateTimeCastor<java.sql.Time, String> {

    @Override
    public String cast(java.sql.Time src, Class<?> toType, String... args) {
        return ((DateFormat) timeFormat.clone()).format(new java.util.Date(src.getTime()));
    }

}
