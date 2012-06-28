package org.nutz.castor.castor;

import java.text.DateFormat;
import java.util.Calendar;

public class Calendar2String extends DateTimeCastor<Calendar, String> {

    @Override
    public String cast(Calendar src, Class<?> toType, String... args) {
        return ((DateFormat) dateTimeFormat.clone()).format(src.getTime());
    }

}
