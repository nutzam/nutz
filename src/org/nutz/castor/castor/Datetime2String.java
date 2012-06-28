package org.nutz.castor.castor;

import java.text.DateFormat;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {

    @Override
    public String cast(java.util.Date src, Class<?> toType, String... args) {
        return ((DateFormat) dateTimeFormat.clone()).format(src);
    }

}
