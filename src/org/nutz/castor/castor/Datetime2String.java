package org.nutz.castor.castor;

import org.nutz.lang.Times;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {

    @Override
    public String cast(java.util.Date src, Class<?> toType, String... args) {
        return Times.sDT(src);
    }

}
