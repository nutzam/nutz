package org.nutz.castor.castor;

import java.text.DateFormat;

public class SqlDate2String extends DateTimeCastor<java.sql.Date, String> {

    @Override
    public String cast(java.sql.Date src, Class<?> toType, String... args) {
        return ((DateFormat) dateFormat.clone()).format(new java.util.Date(src.getTime()));
    }
}
