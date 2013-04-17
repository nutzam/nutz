package org.nutz.castor.castor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class String2Timestamp extends DateTimeCastor<String, Timestamp> {

    @Override
    public Timestamp cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return null;
        try {
            Date d;
            // 格式为 "yyyy-MM-dd HH:mm:ss"
            if (src.length() > 10) {
                d = ((DateFormat) dateTimeFormat.clone()).parse(src);
            }
            // 格式为 "yyyy-MM-dd"
            else {
                d = ((DateFormat) dateFormat.clone()).parse(src);
            }
            return new java.sql.Timestamp(d.getTime());
        }
        catch (ParseException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
