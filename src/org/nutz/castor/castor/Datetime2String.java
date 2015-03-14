package org.nutz.castor.castor;

import org.nutz.lang.Times;

public class Datetime2String extends DateTimeCastor<java.util.Date, String> {
    private String format = "yyyy-MM-dd HH:mm:ss";

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    @Override
    public String cast(java.util.Date src, Class<?> toType, String... args) {
        //return Times.sDT(src);
        return Times.format(format, src);
    }

}
