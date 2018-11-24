package org.nutz.castor.castor;

import org.nutz.lang.Strings;

public class String2Datetime extends DateTimeCastor<String, java.util.Date> {

    @Override
    public java.util.Date cast(String src, Class<?> toType, String... args) {
        // 处理空白
        if (Strings.isBlank(src))
            return null;
        return toDate(src);
    }

}
