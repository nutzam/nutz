package org.nutz.castor.castor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTime2String extends DateTimeCastor<LocalDateTime, String> {
    
    private String format = "yyyy-MM-dd HH:mm:ss";

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    @Override
    public String cast(LocalDateTime src, Class<?> toType, String... args) {
        return DateTimeFormatter.ofPattern(format).format(src);
    }

}
