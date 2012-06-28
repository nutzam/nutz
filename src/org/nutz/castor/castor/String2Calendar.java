package org.nutz.castor.castor;

import java.util.Calendar;

import org.nutz.lang.Strings;
import org.nutz.lang.Times;

public class String2Calendar extends DateTimeCastor<String, Calendar> {

    @Override
    public Calendar cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        Calendar c = Calendar.getInstance();
        c.setTime(Times.parseWithoutException(dateTimeFormat, src));
        return c;
    }

}
