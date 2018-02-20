package org.nutz.castor.castor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.nutz.castor.Castor;
import org.nutz.lang.Lang;

public class LocalDatetime2String extends Castor<TemporalAccessor, String> {
    
    @Override
    public String cast(TemporalAccessor src, Class<?> toType, String... args) {
        if (src instanceof LocalDateTime)
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(src);
        if (src instanceof LocalTime)
            return DateTimeFormatter.ofPattern("HH:mm:ss").format(src);
        if (src instanceof LocalDate)
            return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(src);
        throw Lang.noImplement();
    }

}
