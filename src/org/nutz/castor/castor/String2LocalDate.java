package org.nutz.castor.castor;

import java.time.LocalDate;

import org.nutz.lang.Strings;

public class String2LocalDate extends DateTimeCastor<String, LocalDate> {

    @Override
    public LocalDate cast(String src, Class<?> toType, String... args) {
        // 处理空白
        if (Strings.isBlank(src))
            return null;
        return LocalDate.parse(src);
    }
}
