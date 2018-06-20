package org.nutz.castor.castor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.nutz.castor.Castor;

public class LocalDate2String extends Castor<LocalDate, String> {
    
    @Override
    public String cast(LocalDate src, Class<?> toType, String... args) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(src);
    }

}
