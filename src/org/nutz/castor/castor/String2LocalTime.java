package org.nutz.castor.castor;

import java.time.LocalTime;

import org.nutz.lang.Strings;

public class String2LocalTime extends DateTimeCastor<String, LocalTime> {

    @Override
    public LocalTime cast(String src, Class<?> toType, String... args) {
        // 处理空白
        if (Strings.isBlank(src))
            return null;
        return LocalTime.parse(src);
    }
}
