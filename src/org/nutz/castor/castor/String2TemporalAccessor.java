package org.nutz.castor.castor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.nutz.lang.Strings;

public class String2TemporalAccessor extends DateTimeCastor<String, TemporalAccessor> {

    @Override
    public TemporalAccessor cast(String src, Class<?> toType, String... args) {
        // 处理空白
        if (Strings.isBlank(src))
            return null;
        Date date = toDate(src);
        LocalDateTime dt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        if (toType == LocalDateTime.class)
            return dt;
        if (toType == LocalDate.class)
            return dt.toLocalDate();
        return dt.toLocalTime();
    }

}
