package org.nutz.castor.castor;

import java.text.DateFormat;
import java.text.ParseException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class String2SqlTime extends DateTimeCastor<String, java.sql.Time> {

    @Override
    public java.sql.Time cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        try {
            return new java.sql.Time(((DateFormat) timeFormat.clone()).parse(src).getTime());
        }
        catch (ParseException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
