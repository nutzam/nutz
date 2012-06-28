package org.nutz.castor.castor;

import java.text.ParseException;

import org.nutz.lang.Strings;
import org.nutz.lang.Times;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

    @Override
    public java.util.Date cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        try {
            return Times.parse(dateTimeFormat, src);
        }
        catch (ParseException e1) {
            return Times.parseWithoutException(dateFormat, src);
        }
    }

}
