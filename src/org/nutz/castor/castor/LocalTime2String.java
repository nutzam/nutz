package org.nutz.castor.castor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.nutz.castor.Castor;

public class LocalTime2String extends Castor<LocalTime, String> {
    
    @Override
    public String cast(LocalTime src, Class<?> toType, String... args) {
        return DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(src);
    }

}
