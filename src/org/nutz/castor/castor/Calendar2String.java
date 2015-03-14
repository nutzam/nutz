package org.nutz.castor.castor;

import java.util.Calendar;

import org.nutz.lang.Times;

public class Calendar2String extends DateTimeCastor<Calendar, String> {

    @Override
    public String cast(Calendar src, Class<?> toType, String... args) {
        return Times.sDT(src.getTime());
    }

}
