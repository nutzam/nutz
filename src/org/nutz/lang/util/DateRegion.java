package org.nutz.lang.util;

import java.util.Date;

import org.nutz.lang.Strings;
import org.nutz.lang.Times;

public class DateRegion extends Region<Date> {

    public DateRegion() {
        super();
    }

    public DateRegion(String str) {
        super();
        this.valueOf(str);
    }

    public Date fromString(String str) {
        str = Strings.trim(str);
        if (Strings.isEmpty(str))
            return null;
        return Times.D(str);
    }

    public String toString(Date d) {
        String str = Times.sDT(d);
        if (str.endsWith(" 00:00:00"))
            return str.substring(0, 10);
        return str;
    }
}
