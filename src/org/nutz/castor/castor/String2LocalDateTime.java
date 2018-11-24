package org.nutz.castor.castor;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.nutz.lang.Strings;

public class String2LocalDateTime extends DateTimeCastor<String, LocalDateTime> {

    @Override
    public LocalDateTime cast(String src, Class<?> toType, String... args) {
        // 处理空白
        if (Strings.isBlank(src))
            return null;
        return LocalDateTime.ofInstant(toDate(src).toInstant(), ZoneId.systemDefault());
    }
}
