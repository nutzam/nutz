package org.nutz.castor.castor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.nutz.castor.Castor;

public class Number2LocalDatetime extends Castor<Number, TemporalAccessor> {

    @Override
    public TemporalAccessor cast(Number src, Class<?> toType, String... args) {
        Date date = new Date(src.longValue());
        LocalDateTime dt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        if (toType == LocalDateTime.class)
            return dt;
        if (toType == LocalDate.class)
            return dt.toLocalDate();
        return dt.toLocalTime();
    }

}
