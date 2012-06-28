package org.nutz.castor.castor;

import java.text.DateFormat;
import java.text.ParseException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class String2SqlDate extends DateTimeCastor<String, java.sql.Date> {

    @Override
    public java.sql.Date cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        try {
            return new java.sql.Date(((DateFormat) dateFormat.clone()).parse(src).getTime());
        }
        catch (ParseException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
